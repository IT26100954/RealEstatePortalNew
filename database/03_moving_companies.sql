-- Apply once after the earlier numbered scripts. No existing table is dropped.
USE real_estate_portal_new;

CREATE TABLE moving_companies (
 id CHAR(36) NOT NULL PRIMARY KEY,
 company_name VARCHAR(150) NOT NULL,
 email VARCHAR(254) NOT NULL,
 phone VARCHAR(20) NOT NULL,
 service_areas VARCHAR(500) NOT NULL,
 description TEXT NOT NULL,
 availability VARCHAR(20) NOT NULL,
 CONSTRAINT chk_moving_availability CHECK (availability IN ('AVAILABLE','UNAVAILABLE'))
) ENGINE=InnoDB;
