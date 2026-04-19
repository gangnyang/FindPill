# FindPill 통합 모노레포

FindPill 프로젝트를 **단일 저장소(모노레포)**로 통합한 루트입니다.
앱(Android), 백엔드(Spring Boot), AI 워커(FastAPI), 모델 실험 코드가 한 저장소 안에서 함께 관리됩니다.

## 1. 프로젝트 목표

사용자가 알약 앞/뒤 사진을 업로드하면,

1. OCR 기반 1차 후보를 빠르게 보여주고
2. YOLO 기반 정밀 매칭 결과를 비동기로 완성한 뒤
3. Polling으로 최종 결과를 앱에 반영

하는 구조를 제공합니다.

핵심은 **응답 체감 속도**와 **지연 상황에서의 안정성**입니다.

## 2. 폴더 구조

- `back_end/med_classification`
: Spring Boot 오케스트레이터 + DB 조회 + Redis 상태 관리

- `FindPill_main/pill-integrated`
: Python FastAPI AI 워커(OCR/YOLO 엔드포인트)

- `front_end/Findpill`
: Android 앱(사진 업로드, 결과 화면, 3초 polling)

- `model_workspace`
: 학습/실험/검증 코드(운영 경로와 분리)

- `FindPill_main/Pill_server`
: 레거시 실행 스크립트 및 패키징 산출물 보관

## 3. 전체 처리 아키텍처

```text
Android App
   -> (1) POST /api/pill/analyze
Spring Boot (Orchestrator)
   -> OCR 워커 호출 (/ocr) [동기]
   -> DB 유사도(Levenshtein) 조회
   -> OCR_DONE 1차 응답(jobId + 후보목록)
   -> YOLO 워커 호출 (/yolo) [비동기]
   -> 결과를 Redis(job:{jobId})에 저장 (TTL 5분)
Android App
   -> (2) GET /api/pill/analyze/{jobId} (3초 polling)
   -> DONE 상태 확인 후 최종 결과 반영
```

## 4. 백엔드 처리 구조 (Spring Boot)

### 4.1 기술 스택

- Spring Boot (Web, JPA)
- MySQL (약 정보 테이블)
- Redis (작업 상태/결과 캐시, TTL 5분)
- Async 처리 (`@EnableAsync`, `@Async`)
- Apache Commons Text (LevenshteinDistance)

### 4.2 백엔드에서 하는 일

1. API 게이트웨이 역할
- 앱 요청의 단일 진입점
- AI 워커 직접 노출 최소화

2. OCR 우선 응답
- `/analyze`에서 OCR을 먼저 수행
- OCR 텍스트를 기반으로 유사도 70% 이상 후보를 즉시 반환

3. YOLO 비동기 후처리
- OCR 응답 후 YOLO 작업을 백그라운드로 실행
- YOLO 결과 + 기존 매칭 로직(`findBestMatch`)로 최종 결과 생성

4. 상태 관리
- Redis Key: `findpill:job:{jobId}`
- 상태(phase/status) + 중간/최종 결과 저장
- 5분 후 자동 만료

### 4.3 상태(Phase) 정의

- `OCR_DONE`: OCR 기반 1차 후보 준비됨
- `YOLO_RUNNING`: YOLO 후처리 진행 중
- `DONE`: 최종 결과 완료
- `FAILED`: 처리 실패

### 4.4 상태(Status) 정의

- `2`: 성공(최종 또는 높은 신뢰)
- `1`: 부분 성공(중간/일부 매칭)
- `0`: 실패 또는 결과 없음

## 5. AI 워커 구조 (FastAPI)

### 5.1 엔드포인트

- `POST /ocr`
: front/back 이미지 OCR 수행, 텍스트 목록 반환

- `POST /yolo`
: YOLO + OCR 매칭 데이터(`label`, `class`, `front`, `back`) 반환

- `POST /upload`
: 레거시 호환용 엔드포인트(기존 흐름 유지)

### 5.2 처리 파이프라인

1. 배경 제거(rembg)
2. YOLO 객체 탐지 및 중복 박스 정리
3. Google Vision OCR
4. OCR 보정 모델 적용
5. 박스-텍스트 매핑

## 6. API 명세 (현재 기준)

### 6.1 분석 시작

- `POST /api/pill/analyze`
- `multipart/form-data`
  - `front`: 앞면 이미지
  - `back`: 뒷면 이미지

응답 예시:

```json
{
  "status": "1",
  "phase": "OCR_DONE",
  "job_id": "2f7f4f9c-...",
  "results": [
    {
      "idx": 123,
      "name": "...",
      "print_front": "DA",
      "print_back": "HVT"
    }
  ]
}
```

### 6.2 폴링 조회

- `GET /api/pill/analyze/{jobId}`

응답 예시:

```json
{
  "status": "2",
  "phase": "DONE",
  "job_id": "2f7f4f9c-...",
  "results": [
    {
      "idx": 123,
      "name": "...",
      "label": "1"
    }
  ]
}
```

## 7. 실행 방법

## 7.1 Redis

```bash
docker run -d --name findpill-redis -p 6379:6379 redis:7-alpine
```

## 7.2 Spring Boot

작업 디렉터리:
`back_end/med_classification`

```bash
./gradlew bootRun
```

또는 배포 jar 실행 시 DB/Redis 파라미터를 환경에 맞게 전달합니다.

## 7.3 AI 워커(FastAPI)

작업 디렉터리:
`FindPill_main/pill-integrated`

```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

## 7.4 Android

작업 디렉터리:
`front_end/Findpill`

Android Studio에서 `app` 모듈 실행.

## 8. 프론트 동작 포인트

- 업로드 요청은 Spring `/api/pill/analyze`로 전송
- 첫 응답에서 `job_id` 저장
- 결과 화면에서 3초 간격 polling
- `phase = DONE` 또는 `status = 2`면 최종 결과로 업데이트

## 9. 인코딩/저장 규칙

- 텍스트 소스는 **UTF-8(권장: BOM 없음)**으로 저장
- Windows 콘솔에서 한글이 깨져 보이면 코드페이지를 UTF-8(`chcp 65001`)로 전환 후 확인

## 10. 운영 시 권장사항

1. Spring을 외부 진입점으로 고정하고 AI 워커는 내부망에서만 노출
2. Redis 만료시간(TTL)과 Polling 주기(현재 3초) 운영 환경에 맞게 튜닝
3. YOLO 추론 지연을 고려해 타임아웃/재시도 정책 분리
4. 모델 파일(`.pt`, `.onnx`)과 실험 산출물은 Git 제외

---

문의/확장 시 우선 확인 경로:

- 백엔드 진입: `back_end/med_classification/src/main/java/.../controller/PillController.java`
- AI 워커 진입: `FindPill_main/pill-integrated/main.py`
- 앱 업로드/폴링: `front_end/Findpill/app/src/main/java/.../data/repository/UploadImage.kt`