package ua.foxminded.university.services.ingestion.model;

public record ExtractedPdfPage(int pageNumber, String text) {

    public ExtractedPdfPage {
        if (pageNumber < 1) {
            throw new IllegalArgumentException(
                    "Page number must be positive."
            );
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Extracted page text must not be blank."
            );
        }
    }
}