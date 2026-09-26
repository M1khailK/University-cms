package ua.foxminded.university.customexceptions;

public class InvalidPdfContentException extends RuntimeException {

    public InvalidPdfContentException(String message) {
        super(message);
    }

    public InvalidPdfContentException(String message, Throwable cause) {
        super(message, cause);
    }
}