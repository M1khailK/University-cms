package ua.foxminded.university.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class LessonDTO {
    @NotBlank(message = "Set correct name")
    private String name;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Subject subject;
    private Group group;
    private Teacher teacher;
}
