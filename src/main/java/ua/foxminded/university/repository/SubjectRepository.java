package ua.foxminded.university.repository;

import ua.foxminded.university.info.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
}
