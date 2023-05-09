package ua.foxminded.university.services;

public interface UserService<T> {
    void changePassword(String email, String oldPassword, String newPassword);

    String getRole();

    T getByEmail(String email);

}
