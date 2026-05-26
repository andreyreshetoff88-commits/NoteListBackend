CREATE TABLE IF NOT EXISTS countries  (
    id UUID PRIMARY KEY,
    country_code VARCHAR(2) NOT NULL UNIQUE,
    phone_code VARCHAR(5) NOT NULL,
    phone_mask VARCHAR(50) NOT NULL,
    flag_url VARCHAR(255) NOT NULL
    );

INSERT INTO countries (id, country_code, phone_code, phone_mask, flag_url) VALUES
('62a2627f-53fd-4323-b273-b3cf6954129c', 'RU', '7', '+7 (XXX) XXX-XX-XX', 'http://localhost:9000/flags/ru.png'),
('6ffee131-b578-4854-9065-71b86582e668', 'KG', '996', '+996 (XXX) XXX-XXX', 'http://localhost:9000/flags/kg.png');