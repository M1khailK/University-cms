package ua.foxminded.university.services;

import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;

import java.util.List;

public interface StudentService extends EntityService<Student>, UserManagerService {

    String getPasswordById(int id);



}
