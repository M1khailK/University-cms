package ua.foxminded.university.services.ingestion.impl;

import org.junit.jupiter.api.Test;
import ua.foxminded.university.services.ingestion.model.ExtractedPdfPage;
import ua.foxminded.university.services.ingestion.model.LessonMaterialTextChunk;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordBoundaryTextChunkerImplTest {

    private final WordBoundaryTextChunkerImpl chunker =
            new WordBoundaryTextChunkerImpl();

    @Test
    void chunk_shouldPreserveMaterialAndOriginalPageNumbers() {
        List<LessonMaterialTextChunk> chunks = chunker.chunk(
                10,
                List.of(
                        new ExtractedPdfPage(
                                1,
                                "  Intro\n to   Java  "
                        ),
                        new ExtractedPdfPage(
                                3,
                                "Transactions"
                        )
                )
        );

        assertEquals(
                List.of(
                        new LessonMaterialTextChunk(
                                10, 1, 0, "Intro to Java"
                        ),
                        new LessonMaterialTextChunk(
                                10, 3, 0, "Transactions"
                        )
                ),
                chunks
        );
    }

    @Test
    void chunk_shouldCoverAllWordsWithOverlap() {
        String text = IntStream.range(0, 600)
                .mapToObj(number -> "word" + number)
                .collect(Collectors.joining(" "));

        List<LessonMaterialTextChunk> chunks = chunker.chunk(
                10,
                List.of(new ExtractedPdfPage(2, text))
        );

        assertTrue(chunks.size() > 1);

        Set<String> actualWords = new HashSet<>();

        for (int index = 0; index < chunks.size(); index++) {
            LessonMaterialTextChunk chunk = chunks.get(index);

            assertEquals(10, chunk.materialId());
            assertEquals(2, chunk.pageNumber());
            assertEquals(index, chunk.chunkIndex());
            assertTrue(chunk.text().length() <= 1_200);

            actualWords.addAll(
                    Arrays.asList(chunk.text().split(" "))
            );
        }

        Set<String> expectedWords = IntStream.range(0, 600)
                .mapToObj(number -> "word" + number)
                .collect(Collectors.toSet());

        assertEquals(expectedWords, actualWords);

        String firstChunk = chunks.get(0).text();
        String lastWordOfFirstChunk = firstChunk.substring(
                firstChunk.lastIndexOf(' ') + 1
        );

        assertTrue(
                Arrays.asList(chunks.get(1).text().split(" "))
                        .contains(lastWordOfFirstChunk)
        );
    }

    @Test
    void chunk_shouldSplitVeryLongWordWithoutLooping() {
        List<LessonMaterialTextChunk> chunks = chunker.chunk(
                10,
                List.of(new ExtractedPdfPage(1, "x".repeat(2_401)))
        );

        assertEquals(3, chunks.size());
        assertEquals(1_200, chunks.get(0).text().length());
        assertEquals(1_200, chunks.get(1).text().length());
        assertEquals(1, chunks.get(2).text().length());
    }
}