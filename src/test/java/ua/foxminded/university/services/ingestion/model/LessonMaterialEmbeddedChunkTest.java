package ua.foxminded.university.services.ingestion.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LessonMaterialEmbeddedChunkTest {

    private static final LessonMaterialTextChunk TEXT_CHUNK =
            new LessonMaterialTextChunk(
                    10,
                    1,
                    0,
                    "Database transaction boundaries."
            );

    @Test
    void constructor_shouldDefensivelyCopyEmbedding() {
        float[] source = new float[]{0.1f, 0.2f, 0.3f};

        LessonMaterialEmbeddedChunk embeddedChunk =
                new LessonMaterialEmbeddedChunk(
                        TEXT_CHUNK,
                        source
                );

        source[0] = 99.0f;

        assertArrayEquals(
                new float[]{0.1f, 0.2f, 0.3f},
                embeddedChunk.embedding()
        );

        float[] returned = embeddedChunk.embedding();
        returned[1] = 88.0f;

        assertArrayEquals(
                new float[]{0.1f, 0.2f, 0.3f},
                embeddedChunk.embedding()
        );
    }

    @Test
    void constructor_shouldRejectNonFiniteEmbeddingValue() {
        float[] embedding = new float[]{
                0.1f,
                Float.NaN,
                0.3f
        };

        assertThrows(
                IllegalArgumentException.class,
                () -> new LessonMaterialEmbeddedChunk(
                        TEXT_CHUNK,
                        embedding
                )
        );
    }
}