package ua.foxminded.university.customexceptions;

public class InvalidLessonMaterialUploadException extends RuntimeException {

    public InvalidLessonMaterialUploadException(String message) {
        super(message);
    }
}