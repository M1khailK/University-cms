package ua.foxminded.university.repository;

import ua.foxminded.university.info.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
