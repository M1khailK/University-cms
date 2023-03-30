package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.info.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
}
