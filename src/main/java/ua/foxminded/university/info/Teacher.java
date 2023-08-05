package ua.foxminded.university.info;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "teachers")
@Data
@SecondaryTable(name = "users", pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id"))
@SecondaryTable(name = "user_role", pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id"))
@Where(clause = "t1_2.isEnabled = true")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_seq",allocationSize = 1)
    @Column(name = "user_id", length = 50, nullable = false)
    private Integer id;

    @Column(name = "first_name", table = "users", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", table = "users", length = 50, nullable = false)
    private String lastName;

    @Column(name = "email", table = "users", length = 50, nullable = false)
    private String email;
    @Column(name = "password",table = "users",nullable = false)
    private String password;
    @Column(name = "role", table = "user_role", length = 15, nullable = false)
    private String role;
}
