package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.info.AttendanceRecord;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Integer> {
}
