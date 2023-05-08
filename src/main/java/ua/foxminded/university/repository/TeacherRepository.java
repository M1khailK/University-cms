package ua.foxminded.university.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.foxminded.university.info.Teacher;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    @Query(value = "SELECT * FROM users INNER JOIN user_role ON users.user_id = user_role.user_id " +
            "INNER JOIN teachers ON users.user_id = teachers.user_id " +
            "WHERE users.email = ? AND user_role.role = 'TEACHER';",nativeQuery = true)
    Optional<Teacher> findByEmail(String email);

    @Query(value = "SELECT u.password " +
            "FROM teachers t " +
            "JOIN users u ON t.user_id = u.user_id " +
            "WHERE t.teacher_id = ?", nativeQuery = true)
    String findPasswordById(int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET password = ?1 " +
            "WHERE user_id =  " +
            "(SELECT user_id " +
            "FROM teachers " +
            "WHERE teacher_id = ?2)", nativeQuery = true)
    void changePasswordById(String newPassword,int id);
}
