package ua.foxminded.university.services;

public interface UserService {

    Integer getUserIdByEmail(String email);

    void disableUserById(Integer id);

    }
