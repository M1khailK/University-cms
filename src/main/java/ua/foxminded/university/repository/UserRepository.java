package ua.foxminded.university.repository;

public interface UserRepository {

    Integer findUserIdByEmail(String email);

    void deactivateUserAccountById(Integer id);

    void insertUser(String firstName, String lastName, String email, String password);

    void insertStudent(Integer userId, Integer groupId);

    void insertTeacher(Integer userId);

    void insertAdmin(Integer userId);
}
