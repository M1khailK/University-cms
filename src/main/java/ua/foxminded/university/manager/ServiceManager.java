package ua.foxminded.university.manager;

import ua.foxminded.university.services.UserService;

import java.util.Optional;

public interface ServiceManager {
    void register(String role, UserService service);

    Optional<UserService> getServiceByRole(String role);
}
