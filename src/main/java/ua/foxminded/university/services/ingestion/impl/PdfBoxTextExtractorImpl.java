package ua.foxminded.university.services.ingestion.impl;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import ua.foxminded.university.customexceptions.InvalidPdfContentException;
import ua.foxminded.university.services.ingestion.model.ExtractedPdfPage;
import ua.foxminded.university.services.ingestion.PdfTextExtractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PdfBoxTextExtractorImpl implements PdfTextExtractor {

    private static final int MAX_PAGES = 200;
    private static final int MAX_EXTRACTED_CHARACTERS = 500_000;

    @Override
    public List<ExtractedPdfPage> extract(byte[] pdfBytes) {
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new InvalidPdfContentException("PDF file is empty.");
        }

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            int pageCount = document.getNumberOfPages();

            if (pageCount == 0) {
                throw new InvalidPdfContentException(
                        "PDF does not contain pages."
                );
            }

            if (pageCount > MAX_PAGES) {
                throw new InvalidPdfContentException(
                        "PDF exceeds the supported page limit."
                );
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            List<ExtractedPdfPage> pages = new ArrayList<>();
            int extractedCharacters = 0;

            for (int pageNumber = 1;
                 pageNumber <= pageCount;
                 pageNumber++) {
                stripper.setStartPage(pageNumber);
                stripper.setEndPage(pageNumber);

                String text = stripper.getText(document).strip();

                if (text.isEmpty()) {
                    continue;
                }

                extractedCharacters += text.length();

                if (extractedCharacters > MAX_EXTRACTED_CHARACTERS) {
                    throw new InvalidPdfContentException(
                            "PDF exceeds the supported text limit."
                    );
                }

                pages.add(new ExtractedPdfPage(pageNumber, text));
            }

            if (pages.isEmpty()) {
                throw new InvalidPdfContentException(
                        "PDF contains no extractable text."
                );
            }

            return List.copyOf(pages);
        } catch (IOException exception) {
            throw new InvalidPdfContentException(
                    "PDF cannot be read or its text cannot be extracted.",
                    exception
            );
        }
    }
}