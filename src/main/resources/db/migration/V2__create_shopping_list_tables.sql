CREATE TABLE shopping_list (
                               id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               created_at DATETIME NOT NULL default now(),
                               CONSTRAINT fk_shopping_list_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE shopping_list_item (
                                    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                    shopping_list_id INT NOT NULL,
                                    item_name VARCHAR(100) NOT NULL,
                                    item_idx SMALLINT NOT NULL,
                                    item_price INT NOT NULL,
                                    created_at DATETIME NOT NULL default now(),
                                    purchased BOOLEAN NOT NULL DEFAULT FALSE,
                                    CONSTRAINT fk_shopping_list FOREIGN KEY (shopping_list_id) REFERENCES shopping_list(id)
);


CREATE TABLE user_config (
                             user_id BIGINT NOT NULL PRIMARY KEY,
                             spending_limit INT DEFAULT NULL,
                             email_address VARCHAR(100) DEFAULT NULL,
                             receive_email_notifications BOOLEAN NOT NULL DEFAULT TRUE,
                             CONSTRAINT fk_user_config FOREIGN KEY (user_id) REFERENCES app_user(id)
);

