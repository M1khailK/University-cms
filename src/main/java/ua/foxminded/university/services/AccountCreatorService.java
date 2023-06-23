package ua.foxminded.university.services;

import ua.foxminded.university.dto.User;

public interface AccountCreatorService {
    void createUserAccount(User user, String role);

}
