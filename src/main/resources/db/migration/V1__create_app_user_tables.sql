CREATE TABLE app_user (
                          id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          username VARCHAR(50) NOT NULL UNIQUE,
                          password VARCHAR(100) NOT NULL,
                          enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE app_user_role (
                               id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               role VARCHAR(50) NOT NULL,
                               CONSTRAINT fk_role_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);
