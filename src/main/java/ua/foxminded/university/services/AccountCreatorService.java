package ua.foxminded.university.services;

import ua.foxminded.university.dto.User;

public interface AccountCreatorService {
    void createTeacherAccount(User user);

    void createAdminAccount(User user);

    void createStudentAccount(User user);

}
