package ua.foxminded.university.info;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "teachers")
@Data
@SecondaryTable(name = "users", pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id"))
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", length = 50, nullable = false)
    private Integer id;

    @Column(name = "first_name", table = "users", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", table = "users", length = 50, nullable = false)
    private String lastName;

    @Column(name = "email", table = "users", length = 50, nullable = false)
    private String email;
}
