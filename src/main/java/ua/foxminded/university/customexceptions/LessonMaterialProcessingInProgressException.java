package ua.foxminded.university.customexceptions;

public class LessonMaterialProcessingInProgressException
        extends RuntimeException {

    public LessonMaterialProcessingInProgressException(
            String message
    ) {
        super(message);
    }
}