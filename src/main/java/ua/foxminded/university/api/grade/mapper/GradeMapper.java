package ua.foxminded.university.api.grade.mapper;

import org.springframework.stereotype.Component;
import ua.foxminded.university.api.grade.dto.GradeResponse;
import ua.foxminded.university.info.Grade;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;

import java.util.List;

@Component
public class GradeMapper {

    public GradeResponse toResponse(Grade grade) {
        Student student = grade.getStudent();
        Lesson lesson = grade.getLesson();

        return new GradeResponse(
                grade.getId(),
                grade.getValue(),
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                lesson.getId(),
                lesson.getName(),
                lesson.getDate(),
                lesson.getStartTime(),
                lesson.getEndTime()
        );
    }

    public List<GradeResponse> toResponses(List<Grade> grades) {
        return grades.stream()
                .map(this::toResponse)
                .toList();
    }
}