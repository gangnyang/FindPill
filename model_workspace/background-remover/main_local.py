from rembg import remove, new_session
from PIL import Image
import io
import os
input_folder = 'C:/Users/HRILAB/Desktop/YOLOtest'                          # 입력 폴더
output_folder = 'C:/Users/HRILAB/Desktop/YOLOtest'                        # 출력 폴더
model_path = 'models/u2net_pill_136000.onnx'           # ONNX 모델 경로

# ONNX 커스텀 모델 세션 초기화
model_path = "models/u2net_pill_136000.onnx"
session = new_session(model_name='u2net_custom', model_path=model_path)
os.makedirs(output_folder, exist_ok=True)

image_extensions = ('.jpg', '.jpeg', '.png', '.bmp', '.tiff')

# ===== 폴더 내 모든 이미지 처리 =====
for filename in os.listdir(input_folder):
    if filename.lower().endswith(image_extensions):
        input_path = os.path.join(input_folder, filename)
        output_path = os.path.join(output_folder, os.path.splitext(filename)[0] + '_no_bg.png')

        with open(input_path, 'rb') as i:
            input_bytes = i.read()

        try:
            output_bytes = remove(input_bytes, session=session)
            print(f"  🔎 output size: {len(output_bytes)} bytes")

            output_image = Image.open(io.BytesIO(output_bytes))
            print(f"  📏 image size: {output_image.size}, mode: {output_image.mode}")

            output_image.save(output_path)
            print(f"✅ 처리 완료: {filename} → {output_path}")

        except Exception as e:
            print(f"❌ 오류 발생: {filename} - {e}")

print("🎉 모든 이미지 처리 완료")


