package ua.foxminded.university.info;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rfids")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RFID {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rfid")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Student student;

    @Column(name = "rfid_tag", unique = true, nullable = false)
    private String rfidTag;
}
