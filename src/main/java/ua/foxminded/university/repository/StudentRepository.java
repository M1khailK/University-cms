package ua.foxminded.university.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.foxminded.university.info.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    @Query(value = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.password, u.isEnabled, s.group_id " +
            "FROM users u " +
            "INNER JOIN user_role ur ON u.user_id = ur.user_id " +
            "INNER JOIN students s ON u.user_id = s.user_id " +
            "WHERE u.email = ?1 AND ur.role = 'STUDENT'", nativeQuery = true)
    Optional<Student> findByEmail(String email);

    @Query(value = "SELECT u.password " +
            "FROM students s " +
            "JOIN users u ON s.user_id = u.user_id " +
            "WHERE s.user_id = ?", nativeQuery = true)
    String findPasswordById(int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET password = ?1 " +
            "WHERE user_id = (SELECT user_id " +
            "FROM students " +
            "WHERE user_id = ?2)", nativeQuery = true)
    void changePasswordById(String newPassword, int id);

    @Query(value = "SELECT user_id FROM users WHERE email = ?", nativeQuery = true)
    Integer findIdByEmail(String email);

    @Query(value = "SELECT s.*, u.email, u.first_name, u.last_name, u.password " +
            "FROM students s " +
            "JOIN users u ON s.user_id = u.user_id " +
            "WHERE u.isEnabled = true", nativeQuery = true)
    List<Student> findAllEnabledStudents();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_role (user_id, role) " +
            "VALUES (?, 'STUDENT')", nativeQuery = true)
    void setStudentRole(int studentId);
}
