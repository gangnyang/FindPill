from ultralytics import YOLO
import os
import re

# 클래스 ID와 한글 클래스 이름 매핑
class_id_to_name = {
    0: '갈색_타원형',
    1: '갈색_원형',
    2: '갈색_장방형',
    3: '갈색_캡슐형',
    4: '갈색_기타',
    5: '노랑_타원형',
    6: '노랑_원형',
    7: '노랑_장방형',
    8: '노랑_캡슐형',
    9: '노랑_기타',
    10: '빨강_타원형',
    11: '빨강_원형',
    12: '빨강_장방형',
    13: '빨강_캡슐형',
    14: '빨강_기타',
    15: '초록_타원형',
    16: '초록_원형',
    17: '초록_장방형',
    18: '초록_캡슐형',
    19: '초록_기타',
    20: '파랑_타원형',
    21: '파랑_원형',
    22: '파랑_장방형',
    23: '파랑_캡슐형',
    24: '파랑_기타',
    25: '하양_타원형',
    26: '하양_원형',
    27: '하양_장방형',
    28: '하양_캡슐형',
    29: '하양_기타',
    30: '기타_기타'
}
# 학습된 모델 불러오기 (경로는 본인 모델 위치에 맞게)
model = YOLO('runs/detect/train8/weights/best.pt')

# 테스트할 이미지 경로
img_path = r'C:\Users\HRILAB\Desktop\YOLOtest\turion_no_bg.png'

# 추론 실행 (results 객체에 결과 저장)
results = model.predict(source=img_path, conf=0.25, save=True, save_txt=True)

# 결과 출력
print(results)
