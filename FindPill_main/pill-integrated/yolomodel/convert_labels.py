import json
import os

# JSON이 들어있는 최상위 폴더 (재귀 탐색)
json_root_dir = r"C:\Users\tkdwn\PycharmProjects\YOLOtest\converted_jsons"

# 라벨 저장 폴더
output_label_dir = r"C:\Users\tkdwn\PycharmProjects\YOLOtest\labels\train"
os.makedirs(output_label_dir, exist_ok=True)

# 기존 라벨 파일들 삭제 (옵션)
for file in os.listdir(output_label_dir):
    if file.endswith(".txt"):
        os.remove(os.path.join(output_label_dir, file))

def convert_json_to_yolo_labels(json_path):
    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    cat_id_list = sorted(cat["id"] for cat in data["categories"])
    cat_id_to_yolo_id = {cat_id: idx for idx, cat_id in enumerate(cat_id_list)}

    image_id_map = {img["id"]: img for img in data["images"]}

    for anno in data["annotations"]:
        image_info = image_id_map.get(anno["image_id"])
        if not image_info:
            continue

        file_name_txt = image_info["file_name"].replace(".png", ".txt")
        label_path = os.path.join(output_label_dir, file_name_txt)

        x, y, w, h = anno["bbox"]
        img_w, img_h = image_info["width"], image_info["height"]

        x_center = (x + w / 2) / img_w
        y_center = (y + h / 2) / img_h
        w_norm = w / img_w
        h_norm = h / img_h

        yolo_class_id = cat_id_to_yolo_id[anno["category_id"]]

        line = f"{yolo_class_id} {x_center:.6f} {y_center:.6f} {w_norm:.6f} {h_norm:.6f}\n"

        with open(label_path, "a") as f:
            f.write(line)

    print(f"✅ 변환 완료: {json_path}")

# json_root_dir 내 모든 JSON 파일 재귀 탐색
for root, dirs, files in os.walk(json_root_dir):
    for file in files:
        if file.endswith(".json"):
            json_path = os.path.join(root, file)
            convert_json_to_yolo_labels(json_path)
