import os
from collections import Counter

label_dir = r"C:\Users\HRILAB\PycharmProjects\yolomodel\Capstone-Design\labels\train"

counter = Counter()

for file in os.listdir(label_dir):
    if file.endswith(".txt"):
        path = os.path.join(label_dir, file)
        with open(path, "r", encoding="utf-8") as f:
            for line in f:
                class_id = int(line.strip().split()[0])
                counter[class_id] += 1

# 클래스별 인스턴스 수 출력
for class_id, count in sorted(counter.items()):
    print(f"Class ID {class_id}: {count} instances")
