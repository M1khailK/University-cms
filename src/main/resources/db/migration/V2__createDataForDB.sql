INSERT INTO groups (group_name) VALUES ('Exact Sciences Group');
INSERT INTO groups (group_name) VALUES ('Linguistics Group');

INSERT INTO subjects (subject_name) VALUES ('Mathematics');
INSERT INTO subjects (subject_name) VALUES ('English');
INSERT INTO subjects (subject_name) VALUES ('Ukrainian');
INSERT INTO subjects (subject_name) VALUES ('French');

INSERT INTO users (user_id,first_name, last_name, email,password) VALUES
(nextval('user_seq'),'Bob', 'First', 'bob.first@example.com','$2a$12$HPxWa4yaBboQQMQByAbN2OMeJB3Q5di3/iWeQ6XfGpPNrGOncsP6G'),
(nextval('user_seq'),'Jack', 'Second', 'jack.second@example.com','$2a$12$w7HM.8a422AOCZHwfKaX7u4YJYINJeMYf58bSb5e/vHomNgD4JYrO'),
(nextval('user_seq'),'Alex', 'Third', 'alex.third@example.com','$2a$12$L79h5hwvCDyszsJv0klFY.6FxjRaglGs.Z/MHtUXu5gNNcq/WKLPm'),
(nextval('user_seq'),'Alice', 'Fourth', 'alice.fourth@example.com','$2a$12$pmdp4e.fjSYY8hA0iV9wWeMQSPYwvzuqVTtfL7h2V9LRYQuZfAxqy'),
(nextval('user_seq'),'Tom', 'Fifth', 'tom.fifth@example.com','$2a$12$6bN37oQO9o185mRub0ZUSeuZCBwX.nYMALnVZlOPpV9J5x6LpinYO'),
(nextval('user_seq'),'Sara', 'Sixth', 'sara.sixth@example.com','$2a$12$lpCnlVIXCatv.AvqngkJb.yTfpZk41cXOZMjsK/.SxhkAToEs.HjC'),
(nextval('user_seq'),'Admin', 'AdminSurname', 'admin@example.com','$2a$12$RrhQL7KBsmIRJUXdRj3o6ubtptUs9lJFZgV1yHtjyoq1RYq7qvDa.');

INSERT INTO teachers (user_id)
VALUES (1), (2), (3);

INSERT INTO students (user_id, group_id)
VALUES (4, 1), (5, 2), (6, 2);

INSERT INTO user_role (user_id, role)
VALUES(1, 'TEACHER'),(2, 'TEACHER'),(3, 'TEACHER'),
(4, 'STUDENT'),(5, 'STUDENT'),(6, 'STUDENT'),
(7,'ADMIN');


INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('Math lesson', 1, 1, 1, '2023-04-27', '10:00:00', '12:00:00');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('English lesson', 2, 2, 2, '2022-04-05 ', '11:00:00', '12:30:00');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('Ukrainian lesson', 2, 3, 2, '2022-04-28 ', '12:00:00', '12:30:00');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('Ukrainian lesson', 2, 3, 2, '2023-01-12 ', '13:00:00', '14:30:00');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('English lesson', 2, 2, 2, '2023-02-16 ', '14:00:00', '15:30:00');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('French lesson', 2, 4, 2, '2023-04-28 ', '15:00:00', '16:30:00');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('French lesson', 2, 4, 2, '2023-05-18 ', '15:30:00', '17:30:00');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('French lesson', 2, 4, 2, '2023-06-21 ', '11:00:00', '13:45:00');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('Ukrainian lesson', 2, 3, 2, '2023-07-28 ', '11:00:00', '12:30:00');
