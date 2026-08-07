package ua.foxminded.university.customexceptions;

public class SubjectNotFoundException extends RuntimeException {

    public SubjectNotFoundException(int subjectId) {
        super("Subject was not found by id: " + subjectId);
    }
}