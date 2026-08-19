package ua.foxminded.university.api.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.foxminded.university.api.admin.dto.AdminCreateRequest;
import ua.foxminded.university.api.admin.dto.AdminResponse;
import ua.foxminded.university.api.admin.mapper.AdminMapper;
import ua.foxminded.university.services.TeacherService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminRestController {

    private final TeacherService teacherService;
    private final AdminMapper adminMapper;

    @PostMapping
    public ResponseEntity<AdminResponse> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        AdminResponse response = adminMapper.toResponse(
                teacherService.createAdminAccount(
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
}