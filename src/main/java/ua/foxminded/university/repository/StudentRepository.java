package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.foxminded.university.info.Student;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    @Query(value = "SELECT * FROM users INNER JOIN user_role ON users.user_id = user_role.user_id " +
            "INNER JOIN students ON users.user_id = students.user_id " +
            "WHERE users.email = ? AND user_role.role = 'STUDENT'", nativeQuery = true)
    Optional<Student> findByEmail(String email);
}
