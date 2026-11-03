-- Apply once after the earlier numbered scripts. No existing table is dropped.
USE real_estate_portal_new;

CREATE TABLE appointments (
 id CHAR(36) NOT NULL PRIMARY KEY,
 buyer_id CHAR(36) NOT NULL,
 property_id CHAR(36) NOT NULL,
 viewing_date DATE NOT NULL,
 viewing_time TIME NOT NULL,
 status VARCHAR(20) NOT NULL,
 notes TEXT NULL,
 CONSTRAINT fk_appointments_buyer FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE RESTRICT,
 CONSTRAINT fk_appointments_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
 CONSTRAINT chk_appointments_status CHECK (status IN ('PENDING','CONFIRMED','REJECTED','CANCELLED','COMPLETED')),
 INDEX idx_appointments_slot (property_id, viewing_date, viewing_time, status)
) ENGINE=InnoDB;
-- The Java service must lock/check the property slot transactionally before
-- confirmation. This index alone does not enforce one confirmed booking.
