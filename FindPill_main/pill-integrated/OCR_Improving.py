from transformers import AutoTokenizer, AutoModelForSeq2SeqLM
import re
from typing import Dict, List

# 저장된 모델 경로
model_path = "./ocr_correction_model"

# 모델과 토크나이저 불러오기
tokenizer = AutoTokenizer.from_pretrained(model_path)
model = AutoModelForSeq2SeqLM.from_pretrained(model_path)

def correct_ocr(ocr_dict: Dict[str, List[Dict]]) -> Dict[str, List[Dict]]:
    """
    OCR 인식 결과 딕셔너리를 받고 보정함함

    :param ocr_dict: {"파일명": [ {"text": ..., "box": [...]}, ... ]}
    :return: {"파일명": [ {"text": (보정된), "box": [...]}, ... ]}
    """
    results = {}

    for filename, items in ocr_dict.items():
        corrected_items = []

        for item in items:
            raw_text = item["text"]
            print(f"보정 전 텍스트: {raw_text}")
            inputs = tokenizer(raw_text, return_tensors="pt").to(model.device)
            output = model.generate(**inputs, max_new_tokens=10)
            decoded = tokenizer.decode(output[0], skip_special_tokens=True)
            print(decoded)
            cleaned = re.sub(r'[^a-zA-Z0-9]', '', decoded).upper()
            if len(cleaned)<=1:
                cleaned = raw_text
            print(f"보정 후 텍스트: {cleaned}")
            corrected_items.append({
                "text": cleaned,
                "box": item["box"]
            })

        results[filename] = corrected_items

    return results