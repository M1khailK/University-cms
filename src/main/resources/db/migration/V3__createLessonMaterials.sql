CREATE TABLE lesson_materials (
                                  material_id SERIAL PRIMARY KEY,
                                  lesson_id INT NOT NULL,
                                  object_key VARCHAR(512) NOT NULL,
                                  original_filename VARCHAR(255) NOT NULL,
                                  content_type VARCHAR(100) NOT NULL,
                                  size_bytes BIGINT NOT NULL,
                                  uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL,

                                  FOREIGN KEY (lesson_id) REFERENCES lessons(lesson_id),

                                  UNIQUE (object_key)
);

CREATE INDEX idx_lesson_materials_lesson_id
    ON lesson_materials(lesson_id);