package ua.foxminded.university.services.ingestion.impl;

import org.springframework.stereotype.Component;
import ua.foxminded.university.services.ingestion.LessonMaterialTextChunker;
import ua.foxminded.university.services.ingestion.model.ExtractedPdfPage;
import ua.foxminded.university.services.ingestion.model.LessonMaterialTextChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class WordBoundaryTextChunkerImpl
        implements LessonMaterialTextChunker {

    private static final int MAX_CHUNK_CHARACTERS = 1_200;
    private static final int OVERLAP_CHARACTERS = 200;

    @Override
    public List<LessonMaterialTextChunk> chunk(
            int materialId,
            List<ExtractedPdfPage> pages
    ) {
        if (materialId < 1) {
            throw new IllegalArgumentException(
                    "Material ID must be positive."
            );
        }

        Objects.requireNonNull(pages, "Pages must not be null.");

        if (pages.isEmpty()) {
            throw new IllegalArgumentException(
                    "Pages must not be empty."
            );
        }

        List<LessonMaterialTextChunk> chunks = new ArrayList<>();
        int previousPageNumber = 0;

        for (ExtractedPdfPage page : pages) {
            Objects.requireNonNull(page, "Page must not be null.");

            if (page.pageNumber() <= previousPageNumber) {
                throw new IllegalArgumentException(
                        "Pages must be in ascending order."
                );
            }

            previousPageNumber = page.pageNumber();
            chunkPage(materialId, page, chunks);
        }

        return List.copyOf(chunks);
    }

    private void chunkPage(
            int materialId,
            ExtractedPdfPage page,
            List<LessonMaterialTextChunk> chunks
    ) {
        String text = page.text()
                .replaceAll("\\s+", " ")
                .strip();

        int start = 0;
        int chunkIndex = 0;

        while (start < text.length()) {
            int end = Math.min(
                    start + MAX_CHUNK_CHARACTERS,
                    text.length()
            );

            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end);

                if (lastSpace > start + MAX_CHUNK_CHARACTERS / 2) {
                    end = lastSpace;
                }
            }

            String chunkText = text.substring(start, end).strip();

            chunks.add(new LessonMaterialTextChunk(
                    materialId,
                    page.pageNumber(),
                    chunkIndex++,
                    chunkText
            ));

            if (end == text.length()) {
                break;
            }

            int overlapStart = Math.max(
                    start + 1,
                    end - OVERLAP_CHARACTERS
            );

            int nextSpace = text.indexOf(' ', overlapStart);

            if (nextSpace >= 0 && nextSpace < end) {
                start = nextSpace + 1;
            } else {
                start = end;
            }
        }
    }
}