from fastapi import FastAPI, UploadFile, File
from fastapi.responses import JSONResponse
import requests
from rembg import remove, new_session
from ultralytics import YOLO
import numpy as np
import io
import math
from PIL import Image
from OCR_Process import ocr_images
from OCR_Improving import correct_ocr
from pathlib import Path
from datetime import datetime
import json
from typing import Dict, List

app = FastAPI()
API_URL = "http://127.0.0.1:9998/api/pill/lookup/batch"  # 여기에 진짜 URL 입력

# ONNX 커스텀 모델 세션 초기화
model_path = "models/u2net_pill_136000.onnx"
session = new_session(model_name='u2net_custom', model_path=model_path)
yolo_model = YOLO('models/best.pt')

_colors = ["갈색", "보라", "빨강", "초록", "파랑", "하양"]
_shapes = ["원형", "타원형", "장방형", "캡슐형", "기타"]
class_id_to_name = {
    idx: f"{color}_{shape}"
    for idx, (color, shape) in enumerate(
        (c, s) for c in _colors for s in _shapes
    )
}
class_id_to_name[30] = "기타_기타"

# IOU 계산 함수
def compute_iou(box1, box2):
    xA = max(box1[0], box2[0])
    yA = max(box1[1], box2[1])
    xB = min(box1[2], box2[2])
    yB = min(box1[3], box2[3])

    interArea = max(0, xB - xA) * max(0, yB - yA)
    boxAArea = (box1[2] - box1[0]) * (box1[3] - box1[1])
    boxBArea = (box2[2] - box2[0]) * (box2[3] - box2[1])
    union = boxAArea + boxBArea - interArea
    return interArea / union if union else 0


# 중복 박스 제거 함수
def deduplicate_boxes(boxes, iou_threshold=0.8):
    kept = []
    used = [False] * len(boxes)

    for i in range(len(boxes)):
        if used[i]:
            continue
        current = boxes[i]
        group = [current]
        used[i] = True
        for j in range(i + 1, len(boxes)):
            if used[j]:
                continue
            iou = compute_iou(current["box"], boxes[j]["box"])
            if iou > iou_threshold:
                group.append(boxes[j])
                used[j] = True
        # 그룹 중 신뢰도가 가장 높은 박스만 유지
        best = max(group, key=lambda b: b["confidence"])
        kept.append(best)
    return kept

# 중심점 계산
def center_of(box):
    x1, y1, x2, y2 = box
    return ((x1 + x2) / 2, (y1 + y2) / 2)

# 거리 계산
def euclidean(p1, p2):
    return math.sqrt((p1[0]-p2[0])**2 + (p1[1]-p2[1])**2)

def save_front_back_images(front_bytes: bytes, back_bytes: bytes):
    temp_dir = Path("temp_uploads")
    temp_dir.mkdir(parents=True, exist_ok=True)
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    front_path = temp_dir / f"front_{timestamp}.png"
    back_path = temp_dir / f"back_{timestamp}.png"
    Image.open(io.BytesIO(front_bytes)).convert("RGB").save(front_path)
    Image.open(io.BytesIO(back_bytes)).convert("RGB").save(back_path)
    return front_path, back_path

def extract_side_texts(ocr_improve_results: Dict[str, List[Dict]]):
    front_texts = []
    back_texts = []
    for filename, items in ocr_improve_results.items():
        bucket = front_texts if "front" in filename else back_texts
        for item in items:
            text = item.get("text")
            if text and text not in bucket:
                bucket.append(text)
    return front_texts, back_texts

def run_detection_with_ocr(front_bytes: bytes, back_bytes: bytes):
    removed_bg = remove(front_bytes, session=session)
    image = Image.open(io.BytesIO(removed_bg)).convert("RGB")
    image_np = np.array(image)

    results = yolo_model.predict(source=image_np, conf=0.25, save=True, save_txt=True)

    raw_boxes = []
    for box in results[0].boxes:
        cls_id = int(box.cls[0])
        confidence = float(box.conf[0])
        x1, y1, x2, y2 = map(float, box.xyxy[0])
        raw_boxes.append({
            "class_id": cls_id,
            "confidence": confidence,
            "box": [x1, y1, x2, y2]
        })

    deduped_boxes = deduplicate_boxes(raw_boxes)
    deduped_boxes.sort(key=lambda b: center_of(b["box"])[0])
    for idx, box in enumerate(deduped_boxes):
        box["label"] = str(idx + 1)

    front_path, back_path = save_front_back_images(front_bytes, back_bytes)
    image_files = [front_path, back_path]

    ocr_results = ocr_images(image_files, "vision_result")
    ocr_improve_results = correct_ocr(ocr_results)

    matched_result = []
    for box in deduped_boxes:
        matched_result.append({
            "label": box["label"],
            "class": class_id_to_name.get(box["class_id"], f"UNKNOWN_{box['class_id']}"),
            "front": None,
            "back": None
        })

    for filename, items in ocr_improve_results.items():
        direction = "front" if "front" in filename else "back"
        for item in items:
            pts = item["box"]
            cx = sum(p[0] for p in pts) / len(pts)
            cy = sum(p[1] for p in pts) / len(pts)
            center = (cx, cy)

            if not matched_result:
                continue

            best_match = min(
                matched_result,
                key=lambda b: euclidean(center, center_of(deduped_boxes[int(b["label"]) - 1]["box"]))
            )

            if best_match[direction] is None:
                best_match[direction] = item["text"]

    num_yolo = len(matched_result)
    num_front = sum(len(items) for name, items in ocr_improve_results.items() if "front" in name)
    num_back = sum(len(items) for name, items in ocr_improve_results.items() if "back" in name)
    status_code = 1 if (num_front > num_yolo or num_back > num_yolo) else 2

    return {"status": status_code, "results": matched_result}, ocr_improve_results

@app.post("/ocr")
async def ocr_only(front: UploadFile = File(...), back: UploadFile = File(...)):
    try:
        front_bytes = await front.read()
        back_bytes = await back.read()
        front_path, back_path = save_front_back_images(front_bytes, back_bytes)
        ocr_results = ocr_images([front_path, back_path], "vision_result")
        ocr_improve_results = correct_ocr(ocr_results)
        front_texts, back_texts = extract_side_texts(ocr_improve_results)
        return JSONResponse(content={
            "status": 2,
            "front_texts": front_texts,
            "back_texts": back_texts
        })
    except Exception as e:
        return JSONResponse(content={
            "status": 0,
            "front_texts": [],
            "back_texts": [],
            "error": str(e)
        })

@app.post("/yolo")
async def yolo_only(front: UploadFile = File(...), back: UploadFile = File(...)):
    try:
        front_bytes = await front.read()
        back_bytes = await back.read()
        detection_result, _ = run_detection_with_ocr(front_bytes, back_bytes)
        return JSONResponse(content=detection_result)
    except Exception as e:
        return JSONResponse(content={"status": 0, "results": [], "error": str(e)})

@app.post("/upload")
async def upload_image(front: UploadFile = File(...), back: UploadFile = File(...)):
    input_bytes = await front.read()
    back_bytes = await back.read()
    log_json, _ = run_detection_with_ocr(input_bytes, back_bytes)

    # 새 결과 리스트
    final_results = []
    final_status = 0
    batch_payload = {"items": log_json["results"]}

    try:
        response = requests.post(API_URL, json=batch_payload, timeout=15)
        if response.status_code == 200:
            batch_response = response.json()
            final_results = batch_response.get("results", [])
            final_status = int(batch_response.get("status", 0))
        else:
            final_results = [None] * len(log_json["results"])
            final_status = 0
    except Exception as e:
        final_results = [None] * len(log_json["results"])
        final_status = 0

    # log_json 덮어쓰기
    log_json = {
        "status": final_status,
        "results": final_results
    }

    # 출력
    print(json.dumps(log_json, ensure_ascii=False, indent=2))
    return JSONResponse(content=log_json)

