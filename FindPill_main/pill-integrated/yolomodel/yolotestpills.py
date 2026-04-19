from ultralytics import YOLO

# 학습된 모델 불러오기 (경로는 본인 모델 위치에 맞게)
model = YOLO('runs/detect/train/weights/best.pt')

# 테스트할 이미지 경로
img_path = 'C:/Users/HRILAB/Downloads/pantek.jpg'

# 추론 실행 (results 객체에 결과 저장)
results = model.predict(source=img_path, conf=0.25, save=True, save_txt=True)

# 결과 출력
print(results)
