-- Apply once after the earlier numbered scripts. No existing table is dropped.
USE real_estate_portal_new;

CREATE TABLE offers (
 id CHAR(36) NOT NULL PRIMARY KEY,
 buyer_id CHAR(36) NOT NULL,
 property_id CHAR(36) NOT NULL,
 amount DECIMAL(15,2) NOT NULL,
 message TEXT NULL,
 status VARCHAR(20) NOT NULL,
 CONSTRAINT fk_offers_buyer FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE RESTRICT,
 CONSTRAINT fk_offers_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
 CONSTRAINT chk_offers_amount CHECK (amount > 0),
 CONSTRAINT chk_offers_status CHECK (status IN ('PENDING','ACCEPTED','REJECTED','WITHDRAWN'))
) ENGINE=InnoDB;
