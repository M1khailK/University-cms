package ua.foxminded.university.repository;

import java.util.Optional;

public interface UserRepository {

    Optional<Integer> findUserIdByEmail(String email);

    void deactivateUserAccountById(Integer id);
}
