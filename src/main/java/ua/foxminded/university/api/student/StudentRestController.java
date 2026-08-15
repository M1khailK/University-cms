package ua.foxminded.university.api.student;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.student.dto.StudentResponse;
import ua.foxminded.university.api.student.mapper.StudentMapper;
import ua.foxminded.university.services.StudentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentRestController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @GetMapping
    public List<StudentResponse> getAllStudents() {
        return studentMapper.toResponses(studentService.getAll());
    }

    @GetMapping("/{id}")
    public StudentResponse getStudentById(@PathVariable int id) {
        return studentMapper.toResponse(studentService.getById(id));
    }
}