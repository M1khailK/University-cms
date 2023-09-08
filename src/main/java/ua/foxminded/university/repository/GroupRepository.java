package ua.foxminded.university.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.foxminded.university.info.Group;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    Optional<Group> findByName(String groupName);

    @Modifying
    @Transactional
    @Query(value = "UPDATE groups " +
            "SET group_name = ?1 " +
            "WHERE group_id = ?2", nativeQuery = true)
    void updateGroupName(String groupName, int groupId);

}
