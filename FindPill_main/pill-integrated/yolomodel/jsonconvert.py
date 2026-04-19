import os
import json

json_root_dir = r"C:\Users\HRILAB\Downloads\166.약품식별 인공지능 개발을 위한 경구약제 이미지 데이터\01.데이터\2.Validation\라벨링데이터"

seen = set()

for root, dirs, files in os.walk(json_root_dir):
    for file in files:
        if file.endswith(".json"):
            json_path = os.path.join(root, file)
            with open(json_path, "r", encoding="utf-8") as f:
                data = json.load(f)
                for img in data.get("images", []):
                    color = img.get("color_class1", "없음")
                    shape = img.get("drug_shape", "없음")
                    key = (color, shape)
                    if key not in seen:
                        seen.add(key)
                        print(f"| color_class1: {color} | drug_shape: {shape}")
