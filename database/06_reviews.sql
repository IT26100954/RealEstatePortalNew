-- Apply once after the earlier numbered scripts. No existing table is dropped.
USE real_estate_portal_new;

CREATE TABLE reviews (
 id CHAR(36) NOT NULL PRIMARY KEY,
 reviewer_id CHAR(36) NOT NULL,
 property_id CHAR(36) NOT NULL,
 rating INT NOT NULL,
 comment TEXT NOT NULL,
 CONSTRAINT fk_reviews_author FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE RESTRICT,
 CONSTRAINT fk_reviews_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
 CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB;
