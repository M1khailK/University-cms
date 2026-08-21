package ua.foxminded.university.api.grade;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.grade.dto.GradeResponse;
import ua.foxminded.university.api.grade.mapper.GradeMapper;
import ua.foxminded.university.services.GradeService;

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
}