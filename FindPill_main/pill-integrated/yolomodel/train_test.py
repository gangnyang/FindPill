from ultralytics import YOLO

def main():
    model = YOLO('yolov8n.pt')  # 사전학습된 기본 모델

    model.train(
        data='pill.yaml',
        epochs=400,
        batch=16,
        imgsz=640,
        workers=0,
        device='0',
        save=True
    )


if __name__ == "__main__":
    main()
