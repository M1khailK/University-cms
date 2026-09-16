package ua.foxminded.university.services;

import org.springframework.security.core.Authentication;

public interface AssistantService {

    String answer(String message, Authentication authentication);
}