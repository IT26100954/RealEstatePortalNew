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
