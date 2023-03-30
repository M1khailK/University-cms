package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.info.Group;

public interface GroupRepository extends JpaRepository<Group, Integer> {
}
