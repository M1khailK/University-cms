package ua.foxminded.university.customexceptions;

public class GradeStudentGroupMismatchException extends RuntimeException {

    public GradeStudentGroupMismatchException(
            Integer studentId,
            Integer lessonId
    ) {
        super(
                "Student id " + studentId
                        + " does not belong to the group of lesson id "
                        + lessonId
        );
    }
}