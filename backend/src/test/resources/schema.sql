CREATE TABLE users (id CHAR(36) PRIMARY KEY, username VARCHAR(40) UNIQUE NOT NULL, password_hash VARCHAR(255) NOT NULL, email VARCHAR(254) NOT NULL, phone VARCHAR(20) NOT NULL, role VARCHAR(20) NOT NULL);



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
) ;




CREATE TABLE moving_companies (
 id CHAR(36) NOT NULL PRIMARY KEY,
 company_name VARCHAR(150) NOT NULL,
 email VARCHAR(254) NOT NULL,
 phone VARCHAR(20) NOT NULL,
 service_areas VARCHAR(500) NOT NULL,
 description TEXT NOT NULL,
 availability VARCHAR(20) NOT NULL,
 CONSTRAINT chk_moving_availability CHECK (availability IN ('AVAILABLE','UNAVAILABLE'))
) ;




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
) ;






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
) ;




CREATE TABLE reviews (
 id CHAR(36) NOT NULL PRIMARY KEY,
 reviewer_id CHAR(36) NOT NULL,
 property_id CHAR(36) NOT NULL,
 rating INT NOT NULL,
 comment TEXT NOT NULL,
 CONSTRAINT fk_reviews_author FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE RESTRICT,
 CONSTRAINT fk_reviews_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
 CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
) ;
