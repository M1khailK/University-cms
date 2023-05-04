package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import ua.foxminded.university.info.Teacher;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    @Query(value = "SELECT * FROM users INNER JOIN user_role ON users.user_id = user_role.user_id " +
            "INNER JOIN teachers ON users.user_id = teachers.user_id " +
            "WHERE users.email = ? AND user_role.role = 'TEACHER';",nativeQuery = true)
    Optional<Teacher> findByEmail(String email);
}
