package ua.foxminded.university.customexceptions;

public class TeacherNotFoundException extends IllegalArgumentException {
    public TeacherNotFoundException(int teacherId) {
        super("Teacher was not found by id: " + teacherId);
    }

    public TeacherNotFoundException(String teacherEmail) {
        super("Teacher was not found by email: " + teacherEmail);
    }

}
