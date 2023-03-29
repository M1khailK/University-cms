package ua.foxminded.university.repository;

import ua.foxminded.university.info.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Integer> {
}
