package ua.foxminded.university.api.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.teacher.dto.TeacherResponse;
import ua.foxminded.university.api.teacher.mapper.TeacherMapper;
import ua.foxminded.university.services.TeacherService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherRestController {

    private final TeacherService teacherService;
    private final TeacherMapper teacherMapper;

    @GetMapping
    public List<TeacherResponse> getAllTeachers() {
        return teacherMapper.toResponses(teacherService.getAll());
    }

    @GetMapping("/{id}")
    public TeacherResponse getTeacherById(@PathVariable int id) {
        return teacherMapper.toResponse(teacherService.getById(id));
    }
}