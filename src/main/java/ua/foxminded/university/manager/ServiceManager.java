package ua.foxminded.university.manager;

import ua.foxminded.university.services.UserManagerService;

import java.util.Optional;

public interface ServiceManager {
    void register(String role, UserManagerService service);

    Optional<UserManagerService> getServiceByRole(String role);

    UserManagerService getUserManagerServiceByAuthentication();
}
