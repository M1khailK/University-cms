package ua.foxminded.university.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class LessonDTO {
    @NonNull
    private Integer id;
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NonNull
    private LocalDate date;
    @NonNull
    private LocalTime startTime;
    @NonNull
    private LocalTime endTime;
    @NonNull
    private Subject subject;
    @NonNull
    private Group group;
    @NonNull
    private Teacher teacher;
}
