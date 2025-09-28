INSERT INTO app_user (username, password, enabled) VALUES
       ('bob', 'm4yD3N', TRUE),
       ('sue', 'm4yD3N', TRUE);

INSERT INTO app_user_role (user_id, role) VALUES
      (1, 'ROLE_USER'),
      (2, 'ROLE_USER');


INSERT INTO shopping_list (user_id, created_at)
VALUES (1, NOW());

INSERT INTO shopping_list_item (shopping_list_id, item_idx,  purchased, item_name, item_price)
VALUES
    (1, 1,  FALSE, 'milk', 90),
    (1, 2,  FALSE, 'bread', 120);

