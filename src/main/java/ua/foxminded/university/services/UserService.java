package ua.foxminded.university.services;

public interface UserService {

    Integer getUserIdByEmail(String email);

    void disableUserById(Integer id);

    void insertUser(String firstName, String lastName, String email, String password);

    void insertStudentById(Integer userId, Integer groupId);

    void insertTeacherById(Integer userId);

    void insertAdmin(Integer userId);

    }
