package ua.foxminded.university.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    @NotBlank(message = "Set correct name")
    private String firstName;

    @NotBlank(message = "Set correct surname")
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 5, message = "Set correct group name")
    private String groupName;

    @Size(min = 5, message = "Password must be longer than 4 characters!")
    private String password;

}
