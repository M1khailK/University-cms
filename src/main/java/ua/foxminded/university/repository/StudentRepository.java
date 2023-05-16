package ua.foxminded.university.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.foxminded.university.info.Student;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    @Query(value = "SELECT * FROM users INNER JOIN user_role ON users.user_id = user_role.user_id " +
            "INNER JOIN students ON users.user_id = students.user_id " +
            "WHERE users.email = ? AND user_role.role = 'STUDENT'", nativeQuery = true)
    Optional<Student> findByEmail(String email);

    @Query(value = "SELECT u.password " +
            "FROM students s " +
            "JOIN users u ON s.user_id = u.user_id " +
            "WHERE s.student_id = ?", nativeQuery = true)
    String findPasswordById(int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET password = ?1 " +
            "WHERE user_id =  " +
            "(SELECT user_id " +
            "FROM students " +
            "WHERE student_id = ?2)", nativeQuery = true)
    void changePasswordById(String newPassword, int id);

    @Query(value = "SELECT user_id FROM users WHERE email = ?", nativeQuery = true)
    Integer findIdByEmail(String email);

    @Query(value = "SELECT s.*, u.email,u.first_name,u.last_name FROM students s JOIN users u ON s.user_id = u.user_id WHERE s.user_id = ?", nativeQuery = true)
   Optional<Student> findStudentByUserId(Integer userId);

}
