package ua.foxminded.university.services;

import ua.foxminded.university.services.model.AccessToken;

public interface AuthService {

    AccessToken login(String email, String password);
}