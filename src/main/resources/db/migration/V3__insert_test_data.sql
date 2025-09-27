-- Insert users with BCrypt passwords
INSERT INTO app_user (username, password, enabled) VALUES
       ('bob', '$2a$10$kE4X4U5cQkYFj9TqD9XfAeWIBv/TjI4JwGQZxwM9LqY1VqWm7I8dS', TRUE),
       ('sue', '$2a$10$kE4X4U5cQkYFj9TqD9XfAeWIBv/TjI4JwGQZxwM9LqY1VqWm7I8dS', TRUE);

-- Assign roles
INSERT INTO app_user_role (user_id, role) VALUES
      (1, 'ROLE_USER'),
      (2, 'ROLE_USER');


INSERT INTO shopping_list (user_id, created_at)
VALUES (1, NOW());

-- Shopping list items in pence
INSERT INTO shopping_list_item (shopping_list_id, item_idx,  purchased, item_name, item_price)
VALUES
    (1, 1,  FALSE, 'milk', 90),
    (1, 2,  FALSE, 'bread', 120);

