package ua.foxminded.university.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.repository.UserRepository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void deactivateUserAccountById(Integer id) {
        entityManager.createNativeQuery("UPDATE users SET isEnabled = FALSE WHERE user_id = ?")
                .setParameter(1, id)
                .executeUpdate();
    }
}
