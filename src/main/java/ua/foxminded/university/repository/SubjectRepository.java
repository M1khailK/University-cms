package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.info.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
}
