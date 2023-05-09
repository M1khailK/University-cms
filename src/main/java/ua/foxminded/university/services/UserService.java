package ua.foxminded.university.services;

import ua.foxminded.university.services.impl.PasswordManagerImpl;

public interface UserService {
    void changePassword(String email, String oldPassword, String newPassword);

    String getRole();

    default void register(PasswordManagerImpl pm) {
        pm.register(getRole(), this);
    }

}
