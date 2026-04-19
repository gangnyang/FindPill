from google.cloud import vision
import os
import io
import cv2
import numpy as np
import time
import re

def ocr_images(image_files, output_dir='visionocr_result'):
    os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = "googlecredentialsocrkeysetting.json"
    # API 키 설정
    
    client = vision.ImageAnnotatorClient()

    # 결과 디렉토리
    os.makedirs(output_dir, exist_ok=True)

    # 결과 리스트
    results = {}

    for img_path in image_files:
        image = cv2.imread(str(img_path))
        if image is None:
            print(f"이미지 로딩 실패: {img_path}")
            continue

        with io.open(img_path, 'rb') as image_file:
            content = image_file.read()

        image_b = vision.Image(content=content)
        response = client.text_detection(image=image_b)

        if response.error.message:
            print(f"오류 발생: {response.error.message}")
            results[os.path.basename(img_path)] = ''
            continue

        annotations = response.text_annotations
        if not annotations:
            print(f"텍스트 감지 실패: {img_path}")
            results[os.path.basename(img_path)] = ''
            continue

        box_info=[]
        for text in annotations[1:]:
            box = [(v.x, v.y) for v in text.bounding_poly.vertices]
            cv2.polylines(image, [np.array(box, dtype=np.int32)], isClosed=True, color=(0, 255, 0), thickness=2)
            cv2.putText(image, text.description, box[0], cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 0, 0), 2)
            cleaned = re.sub(r'[^a-zA-Z0-9]', '', text.description).upper()
            if not cleaned:
                continue 
            box_info.append({
                'text': cleaned,
                'box': box
            })
        
        results[os.path.basename(img_path)] = box_info


        save_path = os.path.join(output_dir, f'vision_{os.path.basename(img_path)}')
        cv2.imwrite(save_path, image)
        print(f"저장 완료: {save_path}")
        print(f"{results}")
    return results