package ua.foxminded.university.services.ingestion;

import ua.foxminded.university.services.ingestion.model.ExtractedPdfPage;

import java.util.List;

public interface PdfTextExtractor {

    List<ExtractedPdfPage> extract(byte[] pdfBytes);
}