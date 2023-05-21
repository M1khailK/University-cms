package ua.foxminded.university.manager.impl;

import org.springframework.stereotype.Service;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceManagerImpl implements ServiceManager {
    private final Map<String, UserManagerService> roleToService = new HashMap<>();

    @Override
    public void register(String role, UserManagerService service) {
        roleToService.put(role, service);
    }

    @Override
    public Optional<UserManagerService> getServiceByRole(String role) {
        return Optional.ofNullable(roleToService.get(role));
    }
}
