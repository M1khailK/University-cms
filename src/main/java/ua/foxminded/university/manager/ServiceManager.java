package ua.foxminded.university.manager;

import org.springframework.security.core.Authentication;
import ua.foxminded.university.services.UserManagerService;

import java.util.List;
import java.util.Optional;

public interface ServiceManager {
    void register(String role, UserManagerService service);

    Optional<UserManagerService> getServiceByRole(String role);

    List<UserManagerService> getUserManagerServices();
}
