import os
import json

root_dir = r"C:\Users\tkdwn\Downloads\166.약품식별 인공지능 개발을 위한 경구약제 이미지 데이터\01.데이터\2.Validation\라벨링데이터"

color_set = set()

for subdir, dirs, files in os.walk(root_dir):
    for file in files:
        if file.endswith(".json"):
            json_path = os.path.join(subdir, file)
            try:
                with open(json_path, "r", encoding="utf-8") as f:
                    data = json.load(f)
                for item in data.get("images", []):
                    for color_field in ["dl_custom_shape"]:
                        colors = item.get(color_field, "")
                        if colors:
                            split_colors = [c.strip() for c in colors.replace(";", ",").split(",") if c.strip()]
                            for c in split_colors:
                                color_set.add(c)
            except Exception as e:
                print(f"Error reading {json_path}: {e}")

color_list = sorted(color_set)
print(f"총 {len(color_list)}개의 색깔을 찾았어요:")
print(color_list)
