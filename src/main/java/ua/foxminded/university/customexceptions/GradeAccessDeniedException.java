package ua.foxminded.university.customexceptions;

public class GradeAccessDeniedException extends RuntimeException {

    public GradeAccessDeniedException(Integer lessonId) {
        super("Teacher is not allowed to manage grades for lesson id: " + lessonId);
    }
}