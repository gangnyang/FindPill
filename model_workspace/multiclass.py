import os
import json
import re

# 1. 색깔 통합 매핑 및 모양 리스트
color_map = {
    '갈색': '갈색',
    '주광색': '노랑',
    '주백색': '하양',
    '노랑': '노랑',
    '전구색': '노랑',
    '분홍': '빨강',
    '빨강': '빨강',
    '초록': '초록',
    '파랑': '파랑',
    '하양': '하양',
    '투명': '하양',
    '기타': '기타'
}

colors = ['갈색', '노랑', '빨강', '초록', '파랑', '하양', '기타']
shapes = ['타원형', '원형', '장방형', '캡슐형', '기타']

# 색깔+모양 조합으로 클래스 만들기
classes = [f"{c}_{s}" for c in colors for s in shapes]
class_to_id = {cls: idx for idx, cls in enumerate(classes)}

json_root_dir = r"C:\Users\HRILAB\Downloads\166.약품식별 인공지능 개발을 위한 경구약제 이미지 데이터\01.데이터\2.Validation\라벨링데이터"
output_label_dir = r"C:\Users\HRILAB\PycharmProjects\yolomodel\Capstone-Design\labels\train"
os.makedirs(output_label_dir, exist_ok=True)

def preprocess_color(raw_color):
    if not raw_color or str(raw_color).lower() == 'none':
        return '기타'
    parts = re.split(r'[,\|]', raw_color)
    for p in parts:
        if p.strip() in color_map:
            return color_map[p.strip()]
    return '기타'

def preprocess_shape(raw_shape):
    if not raw_shape or str(raw_shape).lower() == 'none':
        return '기타'
    return raw_shape.strip()

def convert_json_to_yolo_labels(json_path):
    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    image_id_map = {img["id"]: img for img in data["images"]}

    for anno in data["annotations"]:
        image_info = image_id_map.get(anno["image_id"])
        if not image_info:
            continue

        raw_color = image_info.get("color_class1", "") or image_info.get("color_class2", "")
        mapped_color = preprocess_color(raw_color)

        raw_shape = image_info.get("drug_shape", "")
        mapped_shape = preprocess_shape(raw_shape)

        class_name = f"{mapped_color}_{mapped_shape}"

        if class_name not in class_to_id:
            print(f"[경고] 미등록 클래스: '{class_name}' (파일: {json_path})")
            continue

        class_id = class_to_id[class_name]

        x, y, w, h = anno["bbox"]
        img_w, img_h = image_info["width"], image_info["height"]
        x_center = max(0.0, min(1.0, (x + w / 2) / img_w))
        y_center = max(0.0, min(1.0, (y + h / 2) / img_h))
        w_norm = max(0.0, min(1.0, w / img_w))
        h_norm = max(0.0, min(1.0, h / img_h))

        label_filename = image_info["file_name"].replace(".png", ".txt")
        label_path = os.path.join(output_label_dir, label_filename)

        line = f"{class_id} {x_center:.6f} {y_center:.6f} {w_norm:.6f} {h_norm:.6f}\n"
        with open(label_path, "a", encoding="utf-8") as f:
            f.write(line)

    print(f"✅ 변환 완료: {json_path}")

# 모든 JSON 파일에 대해 실행
for root, dirs, files in os.walk(json_root_dir):
    for file in files:
        if file.endswith(".json"):
            convert_json_to_yolo_labels(os.path.join(root, file))
