package ua.foxminded.university.customexceptions;

public class LessonNotFoundException extends IllegalArgumentException {

    public LessonNotFoundException(int lessonId) {
        super("Lesson was not found by id: " + lessonId);
    }
}