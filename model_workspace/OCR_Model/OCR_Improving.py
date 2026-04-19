from transformers import AutoTokenizer, AutoModelForSeq2SeqLM
import re

# 저장된 모델 경로
model_path = "./ocr_correction_model"

# 모델과 토크나이저 불러오기
tokenizer = AutoTokenizer.from_pretrained(model_path)
model = AutoModelForSeq2SeqLM.from_pretrained(model_path)

def correct_ocr(ocr_dict: dict[str, str]) -> dict[str, str]:
    """
    OCR 인식 결과 딕셔너리를 받고 보정함함

    :param ocr_dict: {"파일명": "OCR 결과과", ...}
    :return: {"파일명": "보정 결과과", ...}
    """
    results = {}

    for filename, ocr_input in ocr_dict.items():
        inputs = tokenizer(ocr_input, return_tensors="pt", padding=True, truncation=True).to(model.device)
        output = model.generate(**inputs, max_new_tokens=10)
        decoded = tokenizer.decode(output[0], skip_special_tokens=True)
        cleaned = re.sub(r'[^a-zA-Z0-9]', '', decoded).upper()
        results[filename] = cleaned

    return results