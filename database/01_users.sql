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