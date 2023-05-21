package ua.foxminded.university.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.foxminded.university.info.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    @Query(value = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.password, u.isEnabled " +
            "FROM users u " +
            "INNER JOIN user_role ur ON u.user_id = ur.user_id " +
            "INNER JOIN teachers t ON u.user_id = t.user_id " +
            "WHERE u.email = ? AND ur.role = 'TEACHER'", nativeQuery = true)
    Optional<Teacher> findByEmail(String email);

    @Query(value = "SELECT u.password " +
            "FROM teachers t " +
            "JOIN users u ON t.user_id = u.user_id " +
            "WHERE t.user_id = ?", nativeQuery = true)
    String findPasswordById(int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET password = ?1 " +
            "WHERE user_id = (SELECT user_id " +
            "FROM teachers " +
            "WHERE user_id = ?2)", nativeQuery = true)
    void changePasswordById(String newPassword, int id);

    @Query(value = "SELECT user_id FROM users WHERE email = ?", nativeQuery = true)
    Integer findIdByEmail(String email);

    @Query(value = "SELECT t.*, u.email, u.first_name, u.last_name FROM teachers t JOIN users u ON t.user_id = u.user_id WHERE t.user_id = ?", nativeQuery = true)
    Optional<Teacher> findTeacherByUserId(Integer userId);

    @Query(value = "SELECT t.*, u.email, u.first_name, u.last_name " +
            "FROM teachers t " +
            "JOIN users u ON t.user_id = u.user_id " +
            "WHERE u.isEnabled = true",nativeQuery = true)
    List<Teacher> findAllEnabledTeachers();
}
