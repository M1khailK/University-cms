package ua.foxminded.university.services;

public interface UserService {

    int getUserIdByEmail(String email);

    void disableUserById(Integer id);

}
