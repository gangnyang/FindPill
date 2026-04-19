# Pill Recognition System (작성자 및 통합 시스템 구축 : 서준원)

## 구성 개요
### Branch 설명
- 각 모듈 별 소스 코드(학습 세팅)은 branch로 분리되어 있습니다.
- [main](http://github.com/illujun/Capstone-Design) branch에서는 실행 가능한 환경을 제공합니다.
- [back_end](https://github.com/illujun/Capstone-Design/tree/back_end) branch에서는 Spring Boot 백엔드 서버의 소스 코드를 제공합니다.
- [front_end](https://github.com/illujun/Capstone-Design/tree/front_end) branch에서는 Android용으로 개발된 API 사용 어플리케이션 예제의 소스 코드를 제공합니다.
- [model_workspace](https://github.com/illujun/Capstone-Design/tree/model_workspace) branch에서는 YOLO 및 배경 제거 모듈의 학습 관련 소스 코드를 제공합니다.
- [Coldlapse/pill-bg-remover](https://github.com/Coldlapse/pill-bg-remover) 에서는 특별히 배경 제거 모듈만 다룹니다.

### 1. Pill_server (Spring Boot 기반 백엔드)
- 알약 데이터베이스 조회 및 클래스 기반 가중치 적용 알고리즘을 처리하는 Spring 백엔드입니다.
- localhost에서 구동되며, 외부 접근은 제한되어 있습니다.
- Python 기반 백엔드(pill-integrated)에서만 내부적으로 API 요청을 보낼 수 있도록 설계되어 있습니다.
- 기본 포트: 9998
- Pill_server 개발 : 이정섭, 전동원

### 2. pill-integrated (Python 기반 백엔드)
- YOLO 및 rembg 기반 알약 이미지 처리 시스템입니다.
- Google Vision의 OCR 모델도 사용합니다.
- 프론트엔드로부터 이미지 스트림 요청을 수신하면:
  1. 배경 제거 (rembg)
  2. YOLO 기반 알약 클래스 유추
  3. OCR 처리 결과와 함께 Pill_server에 API 요청
  4. 최종적으로 Pill_server의 응답을 포함한 결과를 프론트엔드에 반환
- FastAPI 기반 API 서버로 구성되어 있으며, 이미지 처리 및 클래스 유추 기능이 포함되어 있습니다.
- YOLO 모델 작업 : 허상준, U2Net 모델 파인튜닝 작업 : 서준원, OCR 모델 작업 : 전동원

## 실행 환경

### Python 버전
- Python 3.8.12

### Python 패키지 설치

- FastAPI 서버 및 배경제거 모듈
pip install fastapi rembg uvicorn onnxruntime python-multipart

- YOLO 모델
pip install ultralytics

- OCR 모델
pip install transformers datasets google-cloud-vision opencv-python-headless

### Pill_server (Spring Boot) 실행 환경
- JDK 17.0.12 필요

### Google Vision 계정 키
- Google Vision 사용자격을 담은 key 파일이 별도로 필요합니다. **(pill-integrated/googlecredentialsocrkeysetting.json)**

## 실행 방법

### Pill_server 실행

- Windows:   run_server.bat
- Ubuntu: chmod +x run_ubuntu.sh && ./run_ubuntu.sh

### pill-integrated 실행 (FastAPI)

- uvicorn main:app --reload --host 0.0.0.0 --port 8000

## 모델 파일

두 모델 파일은 모두 다음 위치에 있어야 합니다:
    /pill-integrated/models/

| 모델명     | 설명            | 파일명                    | 다운로드 링크              |
|------------|-----------------|---------------------------|----------------------------|
| YOLO 모델  | 알약 객체 탐지용 | best.pt                   | [YOLO 모델 다운로드 링크](https://drive.google.com/file/d/1QKygN9z-YhJoT3SlxCAVPojqyPo973mL/view?usp=sharing) |
| U²-Net 모델 | 배경 제거용     | u2net_pill_136000.onnx    | [U²-Net 모델 다운로드 링크](https://drive.google.com/file/d/1inuIfO1hHVhKxUwxeA0lSy1etcYV_osY/view?usp=sharing) |


## 기타
- OCR 결과는 YOLO 탐지 결과와 매칭되어 Pill_server로 전달됩니다.
- 모든 응답은 JSON 형태로 프론트엔드에 전달됩니다.

## API 엔드포인트

### 업로드 엔드포인트
- URL: http://hostname:8000/upload
- 메서드: POST
- Content-Type: multipart/form-data

### 예제 Request
2개의 이미지 파일(`front`, `back`)을 multipart/form-data 형식으로 전송합니다.

form-data 필드 예:
- front: 알약 전면 이미지 (예: JPEG, PNG)
- back: 알약 후면 이미지 (예: JPEG, PNG)

### 예제 Response
요청된 이미지에 대해 YOLO 및 OCR 처리를 수행한 후, 결과를 Pill_server에 조회 요청하고 아래와 같은 형태의 JSON 응답을 반환합니다.
```
{
  "status": 2,
  "results": [
    {
      "idx": 3,
      "name": "헵세비어정10밀리그램(아데포비어디피복실)",
      "color": "하양",
      "material": "",
      "company": "동아에스티(주)",
      "shape": "원형",
      "print_front": "DA",
      "print_back": "HVT",
      "effect": "",
      "dosage": "",
      "warning": "",
      "image": "https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/147426492165100184",
      "label": "1"
    }
  ]
}
```
- status: 처리 결과 상태
  - 2: 모든 객체 처리 성공
  - 1: 일부 객체만 처리 성공
  - 0: 모두 실패하거나 예외 발생