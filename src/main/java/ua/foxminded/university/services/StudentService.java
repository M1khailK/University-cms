package ua.foxminded.university.services;

import org.springframework.data.domain.Page;
import ua.foxminded.university.info.Student;

public interface StudentService extends EntityService<Student>, UserManagerService<Student> {

    String getPasswordById(int id);

    Page<Student> getAll(int page, int size);

    Student createStudentAccount(String firstName, String lastName, String email, int groupId);

    Student updateStudentProfile(int id, String firstName, String lastName, String email, int groupId);
}