CREATE TABLE moving_companies (
    id CHAR(36) PRIMARY KEY,
    company_name VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    service_areas VARCHAR(500) NOT NULL,
    description TEXT NOT NULL,
    availability VARCHAR(20) NOT NULL,
    CHECK (availability IN ('AVAILABLE','UNAVAILABLE'))
);
