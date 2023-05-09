package ua.foxminded.university.services.impl;

import org.springframework.stereotype.Service;
import ua.foxminded.university.services.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PasswordManager {
    private final Map<String, UserService> roleToService = new HashMap<>();

    public void register(String role, UserService service) {
        roleToService.put(role, service);
    }

    public Optional<UserService> getServiceByRole(String role) {
        return Optional.ofNullable(roleToService.get(role));
    }
}
