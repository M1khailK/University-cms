drop table if exists groups;
drop table if exists students;
drop table if exists teachers;
drop table if exists subjects;
drop table if exists lessons;
drop table if exists users;
drop table if exists user_role;

CREATE TABLE users (
  user_id SERIAL PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL DEFAULT 'password',
  isEnabled BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE groups (
    group_id SERIAL PRIMARY KEY,
    group_name VARCHAR(50) NOT NULL
);

CREATE TABLE subjects (
    subject_id SERIAL PRIMARY KEY,
    subject_name VARCHAR(50) NOT NULL
);

CREATE TABLE teachers (
    teacher_id SERIAL PRIMARY KEY,
    user_id INT,
    FOREIGN KEY(user_id) REFERENCES users(user_id)
);

CREATE TABLE lessons (
    lesson_id SERIAL PRIMARY KEY,
    lesson_name VARCHAR(50) NOT NULL,
    group_id INT,
    subject_id INT,
    teacher_id INT,
    FOREIGN KEY (group_id) REFERENCES groups(group_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id),
    lesson_date DATE,
    start_time TIME,
    end_time TIME
);

CREATE TABLE students (
    student_id SERIAL PRIMARY KEY,
    user_id INT,
    group_id INT,
    FOREIGN KEY(user_id) REFERENCES users(user_id),
    FOREIGN KEY (group_id) REFERENCES groups(group_id)
);

CREATE TABLE user_role(
  user_id INT,
  role VARCHAR(15) NOT NULL,
  PRIMARY KEY (user_id, role)
);

INSERT INTO users (first_name, last_name, email) VALUES
('Bob', 'First', 'bob.first@example.com'),
('Alex', 'Second', 'alex.second@example.com');

INSERT INTO teachers (user_id)
VALUES (1);

INSERT INTO students(user_id)
VALUES (2);

INSERT INTO user_role (user_id, role)
VALUES(1, 'TEACHER'),(2,'STUDENT');

