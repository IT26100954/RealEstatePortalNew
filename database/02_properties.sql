-- Apply once after the earlier numbered scripts. No existing table is dropped.
USE real_estate_portal_new;

CREATE TABLE properties (
 id CHAR(36) NOT NULL PRIMARY KEY,
 owner_id CHAR(36) NOT NULL,
 title VARCHAR(150) NOT NULL,
 description TEXT NOT NULL,
 city VARCHAR(100) NOT NULL,
 address VARCHAR(255) NOT NULL,
 price DECIMAL(15,2) NOT NULL,
 property_type VARCHAR(20) NOT NULL,
 status VARCHAR(20) NOT NULL,
 CONSTRAINT fk_properties_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE RESTRICT,
 CONSTRAINT chk_properties_price CHECK (price > 0),
 CONSTRAINT chk_properties_type CHECK (property_type IN ('HOUSE','APARTMENT','LAND')),
 CONSTRAINT chk_properties_status CHECK (status IN ('AVAILABLE','UNAVAILABLE')),
 INDEX idx_properties_city (city)
) ENGINE=InnoDB;
