INSERT INTO groups (group_name) VALUES ('Exact Sciences Group');
INSERT INTO groups (group_name) VALUES ('Linguistics Group');

INSERT INTO subjects (subject_name) VALUES ('Mathematics');
INSERT INTO subjects (subject_name) VALUES ('English');

INSERT INTO teachers (first_name,last_name,email) VALUES ('Bob','First','firstbob@gmail.com');
INSERT INTO teachers (first_name,last_name,email) VALUES ('Alex','Second','secondalex@gmail.com');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('Math lesson', 1, 1, 1, '2023-04-22', '08:00:00', '10:00:00');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('English lesson', 2, 2, 2, '2023-04-22', '12:00:00', '14:30:00');

INSERT INTO students (group_id, first_name, last_name, email) VALUES (1, 'David', 'Brown', 'dbrown@gmail.com');
INSERT INTO students (group_id, first_name, last_name, email) VALUES (1, 'Emma', 'Johnson', 'ejohnson@gmail.com');
INSERT INTO students (group_id, first_name, last_name, email) VALUES (2, 'Charlie', 'Johnson', 'cjohnson@gmail.com');
INSERT INTO students (group_id, first_name, last_name, email) VALUES (2, 'Harry', 'Murphy', 'hmurphy@gmail.com');
