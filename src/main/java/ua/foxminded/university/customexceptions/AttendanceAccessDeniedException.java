package ua.foxminded.university.customexceptions;

public class AttendanceAccessDeniedException extends RuntimeException {

    public AttendanceAccessDeniedException(Integer lessonId) {
        super("Teacher is not allowed to manage attendance for lesson id: " + lessonId);
    }
}