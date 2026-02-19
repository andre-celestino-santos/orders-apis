MERGE INTO users
    (id, username, password)
    KEY (id)
VALUES
    (1, 'test-admin-user', '$2a$12$SLLf7PgtlXYpNqONiq8TBefDj8COgYy5JU4rClwG0yyR3PFi90QEm'), -- username=test-admin-user - password=test-admin-user
    (2, 'test-user', '$2a$12$4uz1oqLXEel62GIRI1JosuZx4wXbX.pRRhl8Q/.6BK8zZncWwhkbO'); -- username=test-user - password=test-user

MERGE INTO roles
    (id, name)
    KEY (id)
VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_USER');

MERGE INTO user_roles
    (role_id, user_id)
    KEY (role_id,user_id)
VALUES
    (1, 1),
    (2, 1),
    (2, 2);