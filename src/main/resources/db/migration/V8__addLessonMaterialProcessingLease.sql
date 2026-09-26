ALTER TABLE lesson_materials
    ADD COLUMN processing_started_at TIMESTAMP WITH TIME ZONE;

UPDATE lesson_materials
SET processing_started_at = COALESCE(
        uploaded_at,
        created_at
                            )
WHERE status = 'PROCESSING';

ALTER TABLE lesson_materials
    ADD CONSTRAINT chk_lesson_material_processing_started_at
        CHECK (
            (
                status = 'PROCESSING'
                    AND processing_started_at IS NOT NULL
                )
                OR
            (
                status <> 'PROCESSING'
                    AND processing_started_at IS NULL
                )
            );