package ua.foxminded.university.customexceptions;

public class LessonMaterialNotFoundException
        extends IllegalArgumentException {

    public LessonMaterialNotFoundException(int materialId) {
        super("Lesson material was not found by id: " + materialId);
    }
}