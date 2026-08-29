package ua.foxminded.university.services;

import org.springframework.security.core.Authentication;
import ua.foxminded.university.services.model.AccessToken;

public interface TokenService {

    AccessToken issue(Authentication authentication);
}