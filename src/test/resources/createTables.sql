drop table if exists groups;
drop table if exists students;
drop table if exists teachers;
drop table if exists subjects;
drop table if exists lessons;

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
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL
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
    group_id INT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    FOREIGN KEY (group_id) REFERENCES groups(group_id)
);
