package ua.foxminded.university.services;

import ua.foxminded.university.services.impl.PasswordManagerImpl;

import java.util.Optional;

public interface UserService<T> {
    void changePassword(String email, String oldPassword, String newPassword);

    String getRole();

    default void register(PasswordManagerImpl pm) {
        pm.register(getRole(), this);
    }

    T getByEmail(String email);

}
