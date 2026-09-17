CREATE SEQUENCE user_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE users
(
    user_id    INT PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    isEnabled  BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE groups
(
    group_id   SERIAL PRIMARY KEY,
    group_name VARCHAR(50) NOT NULL
);

CREATE TABLE subjects
(
    subject_id   SERIAL PRIMARY KEY,
    subject_name VARCHAR(50) NOT NULL
);

CREATE TABLE teachers
(
    user_id INT PRIMARY KEY
);

CREATE TABLE students
(
    user_id  INT PRIMARY KEY,
    group_id INT
);

CREATE TABLE lessons
(
    lesson_id   SERIAL PRIMARY KEY,
    lesson_name VARCHAR(50) NOT NULL,
    group_id    INT,
    subject_id  INT,
    teacher_id  INT,
    FOREIGN KEY (group_id) REFERENCES groups (group_id),
    FOREIGN KEY (subject_id) REFERENCES subjects (subject_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers (user_id),
    lesson_date DATE,
    start_time  TIME,
    end_time    TIME
);

CREATE TABLE lesson_materials
(
    material_id       SERIAL PRIMARY KEY,
    lesson_id         INT                      NOT NULL,
    object_key        VARCHAR(512)             NOT NULL,
    original_filename VARCHAR(255)             NOT NULL,
    content_type      VARCHAR(100)             NOT NULL,
    expected_size_bytes BIGINT                 NOT NULL,
    actual_size_bytes BIGINT,
    status            VARCHAR(32)              NOT NULL,
    checksum_sha256   VARCHAR(128),
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    uploaded_at       TIMESTAMP WITH TIME ZONE,
    processed_at      TIMESTAMP WITH TIME ZONE,
    s3_version_id     VARCHAR(1024),
    s3_sequencer      VARCHAR(128),
    failure_reason    VARCHAR(1000),

    FOREIGN KEY (lesson_id) REFERENCES lessons (lesson_id),
    UNIQUE (object_key),

    CONSTRAINT chk_lesson_material_status
        CHECK (
            status IN (
                       'PENDING_UPLOAD',
                       'UPLOADED',
                       'PROCESSING',
                       'READY',
                       'FAILED'
                )
            ),

    CONSTRAINT chk_lesson_material_expected_size
        CHECK (expected_size_bytes > 0),

    CONSTRAINT chk_lesson_material_actual_size
        CHECK (actual_size_bytes IS NULL OR actual_size_bytes > 0)
);

CREATE INDEX idx_lesson_materials_lesson_id
    ON lesson_materials (lesson_id);

CREATE INDEX idx_lesson_materials_status
    ON lesson_materials (status);

CREATE TABLE user_role
(
    user_id INT,
    role    VARCHAR(15) NOT NULL,
    PRIMARY KEY (user_id, role)
);