package ua.foxminded.university.api.teacher;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.foxminded.university.api.teacher.dto.TeacherCreateRequest;
import ua.foxminded.university.api.teacher.dto.TeacherResponse;
import ua.foxminded.university.api.teacher.dto.TeacherUpdateRequest;
import ua.foxminded.university.api.teacher.mapper.TeacherMapper;
import ua.foxminded.university.services.TeacherService;

import java.net.URI;
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

    @PostMapping
    public ResponseEntity<TeacherResponse> createTeacher(@Valid @RequestBody TeacherCreateRequest request) {
        TeacherResponse response = teacherMapper.toResponse(
                teacherService.createTeacherAccount(
                        request.firstName(),
                        request.lastName(),
                        request.email()
                )
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public TeacherResponse updateTeacher(
            @PathVariable int id,
            @Valid @RequestBody TeacherUpdateRequest request
    ) {
        return teacherMapper.toResponse(
                teacherService.updateTeacherProfile(
                        id,
                        request.firstName(),
                        request.lastName(),
                        request.email()
                )
        );
    }

}