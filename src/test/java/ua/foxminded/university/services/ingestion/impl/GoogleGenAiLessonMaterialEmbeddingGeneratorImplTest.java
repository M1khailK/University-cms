package ua.foxminded.university.services.ingestion.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.embedding.EmbeddingModel;
import ua.foxminded.university.services.ingestion.model.LessonMaterialEmbeddedChunk;
import ua.foxminded.university.services.ingestion.model.LessonMaterialTextChunk;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleGenAiLessonMaterialEmbeddingGeneratorImplTest {

    private static final int EXPECTED_DIMENSIONS = 3;

    private static final LessonMaterialTextChunk FIRST_CHUNK =
            new LessonMaterialTextChunk(
                    10,
                    1,
                    0,
                    "Transactions provide atomicity."
            );

    private static final LessonMaterialTextChunk SECOND_CHUNK =
            new LessonMaterialTextChunk(
                    10,
                    1,
                    1,
                    "Isolation separates concurrent operations."
            );

    @Mock
    private EmbeddingModel embeddingModel;

    private GoogleGenAiLessonMaterialEmbeddingGeneratorImpl generator;

    @BeforeEach
    void setUp() {
        generator =
                new GoogleGenAiLessonMaterialEmbeddingGeneratorImpl(
                        embeddingModel,
                        EXPECTED_DIMENSIONS
                );
    }

    @Test
    void generate_shouldEmbedChunksInOneBatchAndPreserveOrder() {
        List<LessonMaterialTextChunk> chunks = List.of(
                FIRST_CHUNK,
                SECOND_CHUNK
        );

        List<String> texts = List.of(
                FIRST_CHUNK.text(),
                SECOND_CHUNK.text()
        );

        float[] firstEmbedding =
                new float[]{0.1f, 0.2f, 0.3f};

        float[] secondEmbedding =
                new float[]{0.4f, 0.5f, 0.6f};

        when(embeddingModel.embed(texts))
                .thenReturn(List.of(
                        firstEmbedding,
                        secondEmbedding
                ));

        List<LessonMaterialEmbeddedChunk> result =
                generator.generate(chunks);

        assertEquals(2, result.size());

        assertSame(
                FIRST_CHUNK,
                result.get(0).chunk()
        );

        assertArrayEquals(
                firstEmbedding,
                result.get(0).embedding()
        );

        assertSame(
                SECOND_CHUNK,
                result.get(1).chunk()
        );

        assertArrayEquals(
                secondEmbedding,
                result.get(1).embedding()
        );

        verify(embeddingModel).embed(texts);
    }

    @Test
    void generate_shouldRejectEmptyChunkList() {
        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(List.of())
        );

        verifyNoInteractions(embeddingModel);
    }

    @Test
    void generate_shouldRejectUnexpectedEmbeddingCount() {
        List<LessonMaterialTextChunk> chunks = List.of(
                FIRST_CHUNK,
                SECOND_CHUNK
        );

        List<String> texts = List.of(
                FIRST_CHUNK.text(),
                SECOND_CHUNK.text()
        );

        when(embeddingModel.embed(texts))
                .thenReturn(List.of(
                        new float[]{0.1f, 0.2f, 0.3f}
                ));

        assertThrows(
                IllegalStateException.class,
                () -> generator.generate(chunks)
        );
    }

    @Test
    void generate_shouldRejectUnexpectedDimensions() {
        List<LessonMaterialTextChunk> chunks =
                List.of(FIRST_CHUNK);

        when(embeddingModel.embed(
                List.of(FIRST_CHUNK.text())
        )).thenReturn(List.of(
                new float[]{0.1f, 0.2f}
        ));

        assertThrows(
                IllegalStateException.class,
                () -> generator.generate(chunks)
        );
    }
}