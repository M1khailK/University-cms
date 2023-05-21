package ua.foxminded.university.services;

import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;

import java.util.List;

public interface StudentService extends EntityService<Student>, UserManagerService {

    List<Student> getAllEnabledStudents();


}
