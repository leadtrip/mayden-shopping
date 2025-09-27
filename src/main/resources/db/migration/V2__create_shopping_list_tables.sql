
INSERT INTO users (username, password, enabled) VALUES
    ('mayden', '$2a$10$Z6M8XXQd0dJBNf9g0wrtRO9yC0A6cK4RJRCcd6GVFz1P5zZefl6yK', true);

INSERT INTO authorities (username, authority) VALUES
    ('mayden', 'ROLE_USER');
