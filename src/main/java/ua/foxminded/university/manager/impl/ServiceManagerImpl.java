package ua.foxminded.university.manager.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public UserManagerService getUserManagerServiceByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<UserManagerService> services = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(this::getServiceByRole)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        if (services.isEmpty()) {
            throw new IllegalArgumentException("List of services is empty");
        }
        if (services.size() > 1) {
            throw new IllegalStateException("Multiple services found for the user.");
        }
        return services.get(0);    }
}
