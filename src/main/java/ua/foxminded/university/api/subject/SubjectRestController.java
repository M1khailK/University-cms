package ua.foxminded.university.api.subject;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.subject.dto.SubjectResponse;
import ua.foxminded.university.api.subject.mapper.SubjectMapper;
import ua.foxminded.university.services.SubjectService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectRestController {

    private final SubjectService subjectService;
    private final SubjectMapper subjectMapper;

    @GetMapping
    public List<SubjectResponse> getAllSubjects() {
        return subjectService.getAll()
                .stream()
                .map(subjectMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public SubjectResponse getSubjectById(@PathVariable int id) {
        return subjectMapper.toResponse(subjectService.getById(id));
    }
}