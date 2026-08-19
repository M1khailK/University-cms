package ua.foxminded.university.services;

import ua.foxminded.university.info.Student;

public interface StudentService extends EntityService<Student>, UserManagerService<Student> {

    String getPasswordById(int id);

    Student createStudentAccount(String firstName, String lastName, String email, int groupId);

    Student updateStudentProfile(int id, String firstName, String lastName, String email, int groupId);
}