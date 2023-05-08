package ua.foxminded.university.services;

import ua.foxminded.university.info.Student;

import java.util.Optional;

public interface StudentService extends EntityService<Student> {
    Optional<Student> getByEmail(String email);

    String getPasswordById(int id);

    void changePasswordById(String newPassword,int id);
}
