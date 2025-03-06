CREATE SEQUENCE user_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE users (
  user_id INT PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
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
    user_id INT PRIMARY KEY
);

CREATE TABLE students (
    user_id INT PRIMARY KEY,
    group_id INT
);

CREATE TABLE lessons (
    lesson_id SERIAL PRIMARY KEY,
    lesson_name VARCHAR(50) NOT NULL,
    group_id INT,
    subject_id INT,
    teacher_id INT,
    FOREIGN KEY (group_id) REFERENCES groups(group_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(user_id),
    lesson_date DATE,
    start_time TIME,
    end_time TIME
);

CREATE TABLE user_role (
  user_id INT,
  role VARCHAR(15) NOT NULL,
  PRIMARY KEY (user_id, role)
);