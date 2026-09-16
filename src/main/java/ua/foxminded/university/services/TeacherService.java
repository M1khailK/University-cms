package ua.foxminded.university.services;

import ua.foxminded.university.info.Teacher;

public interface TeacherService extends EntityService<Teacher>, UserManagerService {

    String getPasswordById(int id);

    Teacher createAdminAccount(String firstName, String lastName, String email);

    Teacher createTeacherAccount(String firstName, String lastName, String email);

    Teacher updateTeacherProfile(int id, String firstName, String lastName, String email);

}
