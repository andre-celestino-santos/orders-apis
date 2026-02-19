MERGE INTO users
    (username, password)
    KEY (username)
VALUES
    ('test-admin-user', '$2a$12$SLLf7PgtlXYpNqONiq8TBefDj8COgYy5JU4rClwG0yyR3PFi90QEm'), -- username=test-admin-user - password=test-admin-user
    ('test-user', '$2a$12$4uz1oqLXEel62GIRI1JosuZx4wXbX.pRRhl8Q/.6BK8zZncWwhkbO'); -- username=test-user - password=test-user

MERGE INTO roles
    (name)
    KEY (name)
VALUES
    ('ROLE_ADMIN'),
    ('ROLE_USER');

MERGE INTO user_roles
    (role_id, user_id)
    KEY (role_id,user_id)
SELECT r.id, u.id
FROM roles r, users u
WHERE
(r.name = 'ROLE_ADMIN' AND u.username = 'test-admin-user')
OR
(r.name = 'ROLE_USER' AND u.username = 'test-admin-user')
OR
(r.name = 'ROLE_USER' AND u.username = 'test-user');