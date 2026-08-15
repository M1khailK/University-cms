package ua.foxminded.university.customexceptions;

public class StudentNotFoundException extends IllegalArgumentException {

    public StudentNotFoundException(int studentId) {
        super("Student was not found by id: " + studentId);
    }

    public StudentNotFoundException(String studentEmail) {
        super("Student was not found by email: " + studentEmail);
    }
}