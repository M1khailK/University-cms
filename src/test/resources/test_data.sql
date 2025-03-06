TRUNCATE TABLE users, groups, subjects, teachers, students, lessons, user_role;
ALTER SEQUENCE user_seq RESTART WITH 1;
INSERT INTO users (user_id, first_name, last_name, email, password) VALUES
  (nextval('user_seq'), 'Alex', 'First', 'alex.1@example.com', 'password'),
  (nextval('user_seq'), 'Bob', 'Second', 'bob.2@example.com', 'password'),
  (nextval('user_seq'), 'Bob', 'Third', 'bob.3@example.com', 'password'),
  (nextval('user_seq'), 'Alex', 'Fourth', 'alex.4@example.com', 'password');

INSERT INTO students (user_id) VALUES (1), (2);
INSERT INTO teachers (user_id) VALUES (3), (4);

INSERT INTO user_role (user_id, role) VALUES(1, 'STUDENT'), (2, 'STUDENT'), (3, 'TEACHER'), (4, 'TEACHER');
UPDATE users SET isEnabled = FALSE WHERE user_id IN (1, 3);