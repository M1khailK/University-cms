package ua.foxminded.university.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.info.Teacher;

import java.util.List;
import java.util.Optional;
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    Optional<Teacher> findByEmail(String email);

    @Query(value = "SELECT u.password " +
            "FROM teachers t " +
            "JOIN users u ON t.user_id = u.user_id " +
            "WHERE t.user_id = ?", nativeQuery = true)
    Optional<String> findPasswordById(int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET password = ?1 " +
            "WHERE user_id = (SELECT user_id " +
            "FROM teachers " +
            "WHERE user_id = ?2)", nativeQuery = true)
    void changePasswordById(String newPassword, int id);

}
