CREATE TABLE lesson_material_chunks (
 chunk_id BIGSERIAL PRIMARY KEY,
 material_id INT NOT NULL,
 page_number INT NOT NULL,
 chunk_index INT NOT NULL,
 chunk_text TEXT NOT NULL,

 CONSTRAINT fk_lesson_material_chunks_material
     FOREIGN KEY (material_id)
         REFERENCES lesson_materials (material_id)
         ON DELETE CASCADE,

 CONSTRAINT uq_lesson_material_chunks_position
     UNIQUE (material_id, page_number, chunk_index),

 CONSTRAINT chk_lesson_material_chunks_page
     CHECK (page_number > 0),

 CONSTRAINT chk_lesson_material_chunks_index
     CHECK (chunk_index >= 0),

 CONSTRAINT chk_lesson_material_chunks_text
     CHECK (btrim(chunk_text) <> '')
);