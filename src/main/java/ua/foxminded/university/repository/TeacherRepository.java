package ua.foxminded.university.repository;

import ua.foxminded.university.info.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
}
