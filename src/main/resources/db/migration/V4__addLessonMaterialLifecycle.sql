ALTER TABLE lesson_materials
    RENAME COLUMN size_bytes TO expected_size_bytes;

ALTER TABLE lesson_materials
    ADD COLUMN status VARCHAR(32),
    ADD COLUMN created_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN actual_size_bytes BIGINT,
    ADD COLUMN processed_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN checksum_sha256 VARCHAR(128),
    ADD COLUMN s3_version_id VARCHAR(1024),
    ADD COLUMN s3_sequencer VARCHAR(128),
    ADD COLUMN failure_reason VARCHAR(1000);

UPDATE lesson_materials
SET status = 'UPLOADED',
    created_at = uploaded_at;

ALTER TABLE lesson_materials
    ALTER COLUMN status SET NOT NULL,
ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN uploaded_at DROP NOT NULL;

ALTER TABLE lesson_materials
    ADD CONSTRAINT chk_lesson_material_status
        CHECK (
            status IN (
                       'PENDING_UPLOAD',
                       'UPLOADED',
                       'PROCESSING',
                       'READY',
                       'FAILED'
                )
            ),
    ADD CONSTRAINT chk_lesson_material_expected_size
        CHECK (expected_size_bytes > 0),
    ADD CONSTRAINT chk_lesson_material_actual_size
        CHECK (actual_size_bytes IS NULL OR actual_size_bytes > 0);

CREATE INDEX idx_lesson_materials_status
    ON lesson_materials(status);