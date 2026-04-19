from OCR_Process import ocr_images
from OCR_Improving import correct_ocr

image_files = ["OCR_Recognition/pill_front.jpg", "OCR_Recognition/pill_back.jpg"] # 파일 경로

ocr_results = ocr_images(image_files, "vision_result")
ocr_improve_results = correct_ocr(ocr_results)
print(ocr_improve_results)
