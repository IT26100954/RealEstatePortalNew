# Database reference

Database: real_estate_portal_new. Initial setup order: 00,01,02,03,04,05,06.
These scripts must not be rerun against existing tables; schema migration is a separate reviewed change.
All IDs are UUID text. Foreign keys use ON DELETE RESTRICT. Java creates IDs; MySQL is not using auto increment.
Usernames are unique with a case-insensitive collation; email is not a unique database key.
Moving-company records are independent of other business tables.
Appointments/offers/reviews reference users and properties.

## Logical associations
User 1 -> 0..* Property (owner_id); User 1 -> 0..* Appointment (buyer_id); Property 1 -> 0..* Appointment.
User 1 -> 0..* Offer (buyer_id); Property 1 -> 0..* Offer.
User 1 -> 0..* Review (reviewer_id); Property 1 -> 0..* Review.
These are database ID associations, not @OneToMany/@ManyToOne fields in the Java model.

## 01_users
```sql
USE real_estate_portal_new;

-- Store user accounts.
CREATE TABLE users (
                       id CHAR(36) NOT NULL,

    -- Usernames with unique, ignoring letter case.
                       username VARCHAR(40)
                              CHARACTER SET utf8mb4
                           COLLATE utf8mb4_0900_ai_ci
                                   NOT NULL,

    -- Store only the password hash.
                       password_hash VARCHAR(255) NOT NULL,

                       email VARCHAR(254) NOT NULL,
                       phone VARCHAR(20) NOT NULL,
                       role VARCHAR(20) NOT NULL,

                       PRIMARY KEY (id),
                       CONSTRAINT uq_users_username UNIQUE (username),

    -- Permit only the three agreed roles.
                       CONSTRAINT chk_users_role
                           CHECK (role IN ('BUYER', 'SELLER', 'ADMIN'))
) ENGINE = InnoDB;
```

## 02_properties
```sql
USE real_estate_portal_new;

CREATE TABLE properties (
    id CHAR(36) PRIMARY KEY,
    owner_id CHAR(36) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    property_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE RESTRICT,
    CHECK (price > 0 AND property_type IN ('HOUSE','APARTMENT','LAND') AND status IN ('AVAILABLE','UNAVAILABLE'))
);
```

## 03_moving_companies
```sql
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
```

## 04_appointments
```sql
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
```

## 05_offers
```sql
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
```

## 06_reviews
```sql
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
```

## Storage and concurrency
Money DECIMAL(15,2), positive and below 10^13 in service validation; phone numbers remain text.
Review rating INT with CHECK 1-5; state choices protected by CHECK constraints.
Appointment slot index supports the confirmed-slot lookup; it is not a unique constraint.
Transactional property/appointment locks enforce exact confirmed-slot exclusion.
Repository prepared SQL parameters avoid concatenating user values into queries.
User security mutations lock user rows and invalidate tracked sessions after commit.
Runtime ddl-auto=none and sql.init.mode=never preserve existing schemas. Tests use explicit H2 schema with ddl-auto=none.
