package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.info.RFID;

import java.util.Optional;

@Repository
public interface RFIDRepository extends JpaRepository<RFID, Integer>{
    Optional<RFID> findByRfidTag(String rfidTag);
}
