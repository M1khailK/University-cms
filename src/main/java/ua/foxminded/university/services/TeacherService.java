package ua.foxminded.university.services;

import ua.foxminded.university.info.Teacher;

import java.util.List;

public interface TeacherService extends EntityService<Teacher>, UserManagerService {

    List<Teacher> getAllEnabledTeachers();

    String getPasswordById(int id);

}
