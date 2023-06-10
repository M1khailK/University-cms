package ua.foxminded.university.services;

import ua.foxminded.university.info.Student;

import java.util.List;

public interface StudentService extends EntityService<Student>, UserManagerService {

    List<Student> getAllEnabledStudents();

    String getPasswordById(int id);

}
