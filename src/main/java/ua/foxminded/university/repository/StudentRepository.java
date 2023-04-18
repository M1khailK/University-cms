package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.info.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
