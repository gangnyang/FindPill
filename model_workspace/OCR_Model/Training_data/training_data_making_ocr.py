import io
import json
import requests
import cv2
import numpy as np
from PIL import Image
from google.cloud import vision
import os
import re

def extract_engrave(vision_text: str):
    if not vision_text:
        return []

    lines = vision_text.strip().splitlines() # 줄바뀜 제거
    lines = [re.sub(r"[^A-Z0-9]", "", line.upper()) for line in lines]

    results = [line for line in lines if re.fullmatch(r"[A-Z0-9]{2,10}", line)] 
    # 정규화 식에 따라 2~10자의 영문자, 숫자로 이루어진 글자만 남김

    test = ''.join(lines)
    if re.fullmatch(r"[A-Z0-9]{2,10}", test):
        results.append(test)


    return results

def pick_engrave(results):
    blocked_keywords = [
        "KPIC", "LILLY", "PFIZER", "DAEWON", "YUHAN", "HANMI", "CELLTRION",
        "LOT", "EXP", "ROCHE", "NOVARTIS", "PLIVA", "SAMSUNG", "KUKJE"
    ]

    def is_not_watermark(text):
        return all(blocked not in text for blocked in blocked_keywords)

    filtered = [r for r in results if is_not_watermark(r)]

    return max(filtered, key=len) if filtered else ""
# 잘못 인식되었을 수 있기 때문에 가장 긴 문자를 반환함

os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "googlecredentialsocrkeysetting.json"

client = vision.ImageAnnotatorClient()

with open("ocr_1000_with_back.jsonl", "r", encoding="utf-8") as f:
    records = [json.loads(line) for line in f.readlines()]

results = []

for record in records[:1000]: 
    try:
        image_url = record["image_url"]
        gt = record["ground_truth"]
        gtb = record["ground_truth_back"]

        response = requests.get(image_url)
        image = Image.open(io.BytesIO(response.content)).convert("RGB")
        img_np = np.array(image)
        h, w = img_np.shape[:2]

        top_remove = int(h*0.2)
        bottom_remove = int(h*0.8)
        left_remove = int(w*0.1)
        right_remove = int(w*0.5)
        left_half = img_np[top_remove:bottom_remove, left_remove:right_remove] # 사진의 왼쪽이 앞면이기 때문에 왼쪽만 잘라냄
        _, buf = cv2.imencode(".jpg", left_half)
        image_bytes = buf.tobytes()

        image_vision = vision.Image(content=image_bytes)
        res = client.text_detection(image=image_vision)
        text = res.text_annotations[0].description.strip() if res.text_annotations else ""
        extracted = extract_engrave(text)
        result = pick_engrave(extracted)
        if (gt!="" and result==gt) or (gtb!="" and result==gtb):
            correct = True
        else:
            correct = False
        results.append({
            "image_url": image_url,
            "ground_truth": gt,
            "ground_truth_back": gtb,
            "vision_result": result,
            "correct" : correct
        })
    except Exception as e:
        results.append({
            "image_url": record.get("image_url"),
            "ground_truth": record.get("ground_truth"),
            "vision_result": f"ERROR: {str(e)}"
        })

with open("ocr_pill_vision_results.jsonl", "w", encoding="utf-8") as f:
    for item in results:
        f.write(json.dumps(item, ensure_ascii=False) + "\n")


