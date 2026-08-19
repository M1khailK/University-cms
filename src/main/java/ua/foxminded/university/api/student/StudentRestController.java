package ua.foxminded.university.api.student;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.foxminded.university.api.student.dto.StudentCreateRequest;
import ua.foxminded.university.api.student.dto.StudentResponse;
import ua.foxminded.university.api.student.dto.StudentUpdateRequest;
import ua.foxminded.university.api.student.mapper.StudentMapper;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentRestController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;
    private final UserService userService;

    @GetMapping
    public List<StudentResponse> getAllStudents() {
        return studentMapper.toResponses(studentService.getAll());
    }

    @GetMapping("/{id}")
    public StudentResponse getStudentById(@PathVariable int id) {
        return studentMapper.toResponse(studentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentCreateRequest request) {
        StudentResponse response = studentMapper.toResponse(
                studentService.createStudentAccount(
                        request.firstName(),
                        request.lastName(),
                        request.email(),
                        request.groupId()
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
    public StudentResponse updateStudent(
            @PathVariable int id,
            @Valid @RequestBody StudentUpdateRequest request
    ) {
        return studentMapper.toResponse(
                studentService.updateStudentProfile(
                        id,
                        request.firstName(),
                        request.lastName(),
                        request.email(),
                        request.groupId()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateStudent(@PathVariable int id) {
        studentService.getById(id);
        userService.disableUserById(id);
        return ResponseEntity.noContent().build();
    }
}