create table groups (
    group_id SERIAL PRIMARY KEY,
    group_name VARCHAR(50) NOT NULL
);
create table subjects(
    subject_id SERIAL PRIMARY KEY,
    subject_name VARCHAR(50) NOT NULL
);
create table lessons(
    lesson_id SERIAL PRIMARY KEY,
    lesson_name VARCHAR(50) NOT NULL,
    group_id INT,
    subject_id INT,
    teacher_id INT,
    lesson_date DATE,
    start_time TIME,
    end_time TIME
);
create table students(
	student_id SERIAL PRIMARY KEY,
	group_id INT,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	email VARCHAR(50) NOT NULL
);
create table teachers(
	teacher_id SERIAL PRIMARY KEY,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	email VARCHAR(50) NOT NULL
);