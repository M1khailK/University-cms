package ua.foxminded.university.services.ingestion.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import ua.foxminded.university.customexceptions.InvalidPdfContentException;
import ua.foxminded.university.services.ingestion.model.ExtractedPdfPage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PdfBoxTextExtractorImplTest {

    private final PdfBoxTextExtractorImpl extractor =
            new PdfBoxTextExtractorImpl();

    @Test
    void extract_shouldPreservePageNumbersAndText() throws IOException {
        byte[] pdf = createPdf(
                "First lesson page",
                null,
                "Third lesson page"
        );

        List<ExtractedPdfPage> pages = extractor.extract(pdf);

        assertEquals(2, pages.size());
        assertEquals(1, pages.get(0).pageNumber());
        assertEquals("First lesson page", pages.get(0).text());
        assertEquals(3, pages.get(1).pageNumber());
        assertEquals("Third lesson page", pages.get(1).text());
    }

    @Test
    void extract_shouldRejectPdfWithoutText() throws IOException {
        byte[] pdf = createPdf((String) null);

        assertThrows(
                InvalidPdfContentException.class,
                () -> extractor.extract(pdf)
        );
    }

    @Test
    void extract_shouldRejectInvalidPdfBytes() {
        byte[] invalid =
                "This is not a PDF".getBytes(StandardCharsets.UTF_8);

        assertThrows(
                InvalidPdfContentException.class,
                () -> extractor.extract(invalid)
        );
    }

    private byte[] createPdf(String... pageTexts) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            for (String pageText : pageTexts) {
                PDPage page = new PDPage();
                document.addPage(page);

                if (pageText != null) {
                    try (PDPageContentStream content =
                                 new PDPageContentStream(document, page)) {
                        content.beginText();
                        content.setFont(
                                new PDType1Font(
                                        Standard14Fonts.FontName.HELVETICA
                                ),
                                12
                        );
                        content.newLineAtOffset(50, 700);
                        content.showText(pageText);
                        content.endText();
                    }
                }
            }

            document.save(output);
            return output.toByteArray();
        }
    }
}