package ua.foxminded.university.api.grade.mapper;

import org.springframework.stereotype.Component;
import ua.foxminded.university.api.grade.dto.GradeLessonOptionResponse;
import ua.foxminded.university.api.grade.dto.GradeOptionsResponse;
import ua.foxminded.university.api.grade.dto.GradeResponse;
import ua.foxminded.university.api.grade.dto.GradeStudentOptionResponse;
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

    public GradeOptionsResponse toOptionsResponse(List<Lesson> lessons) {
        List<GradeLessonOptionResponse> lessonOptions = lessons.stream()
                .map(this::toLessonOptionResponse)
                .toList();

        return new GradeOptionsResponse(lessonOptions);
    }

    private GradeLessonOptionResponse toLessonOptionResponse(
            Lesson lesson
    ) {
        List<GradeStudentOptionResponse> students =
                lesson.getGroup()
                        .getStudents()
                        .stream()
                        .map(this::toStudentOptionResponse)
                        .toList();

        return new GradeLessonOptionResponse(
                lesson.getId(),
                lesson.getName(),
                lesson.getDate(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                lesson.getGroup().getId(),
                lesson.getGroup().getName(),
                students
        );
    }

    private GradeStudentOptionResponse toStudentOptionResponse(
            Student student
    ) {
        return new GradeStudentOptionResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()
        );
    }
}