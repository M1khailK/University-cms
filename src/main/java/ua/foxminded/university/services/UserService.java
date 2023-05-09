package ua.foxminded.university.services;

import ua.foxminded.university.services.impl.PasswordManager;

public interface UserService {
    void changePassword(String email, String oldPassword, String newPassword);

    String getRole();

    default void register(PasswordManager pm) {
        pm.register(getRole(), this);
    }

}
