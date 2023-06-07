package ua.foxminded.university.repository;

public interface UserRepository {

    Integer findUserIdByEmail(String email);

    void deactivateUserAccountById(Integer id);
}
