 CREATE TABLE drug (
    idx VARCHAR(50) PRIMARY KEY,           -- 약 고유 ID (직접 입력)
    name VARCHAR(255) NOT NULL,            -- 약 이름
    color VARCHAR(100),                  -- 약 색상
    material VARCHAR(255),                 -- 약 재료
    company VARCHAR(255),                  -- 제조 회사
    shape VARCHAR(100),                  -- 약 모양
    divided VARCHAR(50),                 --
    print_front VARCHAR(100),                 -- 앞면 각인
    print_back VARCHAR(100),                  -- 뒷면 각인
    purpose TEXT,                               -- 사용 용도 (의미: 어떤 목적으로 복용하는지)
    effect TEXT,                              -- 효과 (예: 통증 완화 등)
    dosage TEXT,                              -- 용법 (예: 하루 3번 등)
    warning TEXT,                              -- 주의사항 (di_warning)
     image TEXT                  -- 이미지 URL
);