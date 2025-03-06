package ua.foxminded.university.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    @NotBlank(message = "Name must not be blank")
    private String firstName;

    @NotBlank(message = "Surname must not be blank")
    private String lastName;

    @Email(message = "Set valid email")
    private String email;

    @Size(min = 5, message = "Set correct group name")
    private String groupName;

    @Size(min = 5, message = "Set password longer than 4 characters!")
    private String password;

    private String role;

}
