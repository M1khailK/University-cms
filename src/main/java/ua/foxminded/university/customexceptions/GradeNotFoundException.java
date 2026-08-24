package ua.foxminded.university.customexceptions;

public class GradeNotFoundException extends RuntimeException {

    public GradeNotFoundException(Integer id) {
        super("Grade was not found by id: " + id);
    }
}