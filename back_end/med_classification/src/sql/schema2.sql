CREATE TABLE drug (
    idx INT AUTO_INCREMENT PRIMARY KEY,      -- 약 고유 ID (자동 증가)
    name VARCHAR(255) NOT NULL,              -- 약 이름
    color VARCHAR(100),                      -- 약 색상
    material VARCHAR(255),                   -- 약 재료
    company VARCHAR(255),                    -- 제조 회사
    shape VARCHAR(100),                      -- 약 모양
    print_front VARCHAR(100),                -- 앞면 각인
    print_back VARCHAR(100),                 -- 뒷면 각인
    effect TEXT,                             -- 효과
    dosage TEXT,                             -- 용법
    warning TEXT,                            -- 주의사항
    image TEXT                               -- 이미지 URL
);