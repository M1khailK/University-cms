TRUNCATE TABLE groups CASCADE;
TRUNCATE TABLE subjects CASCADE;
TRUNCATE TABLE teachers CASCADE;
TRUNCATE TABLE lessons CASCADE;
TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE students CASCADE;
TRUNCATE TABLE user_role CASCADE;

INSERT INTO groups (group_name) VALUES ('Exact Sciences Group');
INSERT INTO groups (group_name) VALUES ('Linguistics Group');

INSERT INTO subjects (subject_name) VALUES ('Mathematics');
INSERT INTO subjects (subject_name) VALUES ('English');

INSERT INTO users (first_name, last_name, email,password) VALUES
('Bob', 'First', 'bob.first@example.com','$2a$12$HPxWa4yaBboQQMQByAbN2OMeJB3Q5di3/iWeQ6XfGpPNrGOncsP6G'),
('Jack', 'Second', 'jack.second@example.com','$2a$12$w7HM.8a422AOCZHwfKaX7u4YJYINJeMYf58bSb5e/vHomNgD4JYrO'),
('Alex', 'Third', 'alex.third@example.com','$2a$12$L79h5hwvCDyszsJv0klFY.6FxjRaglGs.Z/MHtUXu5gNNcq/WKLPm'),
('Alice', 'Fourth', 'alice.fourth@example.com','$2a$12$pmdp4e.fjSYY8hA0iV9wWeMQSPYwvzuqVTtfL7h2V9LRYQuZfAxqy'),
('Tom', 'Fifth', 'tom.fifth@example.com','$2a$12$6bN37oQO9o185mRub0ZUSeuZCBwX.nYMALnVZlOPpV9J5x6LpinYO'),
('Sara', 'Sixth', 'sara.sixth@example.com','$2a$12$lpCnlVIXCatv.AvqngkJb.yTfpZk41cXOZMjsK/.SxhkAToEs.HjC');

INSERT INTO teachers (user_id)
VALUES (1), (2), (3);

INSERT INTO students (user_id, group_id)
VALUES (4, 1), (5, 2), (6, 2);

INSERT INTO user_role (user_id, role)
VALUES(1, 'TEACHER'),(2, 'TEACHER'),(3, 'TEACHER'),
(4, 'STUDENT'),(5, 'STUDENT'),(6, 'STUDENT');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('Math lesson', 1, 1, 1, '2023-04-27', '10:00:00', '12:00:00');

INSERT INTO lessons (lesson_name, group_id, subject_id, teacher_id, lesson_date, start_time, end_time)
VALUES ('English lesson', 2, 2, 2, '2023-04-28 ', '11:00:00', '12:30:00');
