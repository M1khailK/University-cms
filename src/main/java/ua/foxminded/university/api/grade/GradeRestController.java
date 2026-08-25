package ua.foxminded.university.api.grade;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.grade.dto.GradeResponse;
import ua.foxminded.university.api.grade.mapper.GradeMapper;
import ua.foxminded.university.services.GradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ua.foxminded.university.api.grade.dto.GradeCreateRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeRestController {

    private final GradeService gradeService;
    private final GradeMapper gradeMapper;

    @GetMapping
    public List<GradeResponse> getGrades() {
        return gradeMapper.toResponses(gradeService.getAllGrades());
    }

    @GetMapping("/me")
    public List<GradeResponse> getMyGrades(Authentication authentication) {
        return gradeMapper.toResponses(gradeService.getGradesByEmail(authentication.getName()));
    }
    @PostMapping
    public ResponseEntity<GradeResponse> createGrade(@Valid @RequestBody GradeCreateRequest request,
                                                     Authentication authentication) {
        GradeResponse response = gradeMapper.toResponse(
                gradeService.createGrade(
                        request.studentId(),
                        request.lessonId(),
                        request.value(),
                        authentication.getName()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Integer id,
                                            Authentication authentication) {
        gradeService.deleteGrade(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}