package ua.foxminded.university.services;

import ua.foxminded.university.dto.User;

public interface PasswordService {
    void generateAndSendPasswordForUser(User user);

}
