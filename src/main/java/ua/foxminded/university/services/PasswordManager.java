package ua.foxminded.university.services;

import java.util.Optional;

public interface PasswordManager {
    void register(String role, UserService service);

    Optional<UserService> getServiceByRole(String role);
}
