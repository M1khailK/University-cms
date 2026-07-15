package ua.foxminded.university.api.subject;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.foxminded.university.api.subject.dto.SubjectCreateRequest;
import ua.foxminded.university.api.subject.dto.SubjectResponse;
import ua.foxminded.university.api.subject.mapper.SubjectMapper;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.services.SubjectService;
import org.springframework.web.bind.annotation.PutMapping;
import ua.foxminded.university.api.subject.dto.SubjectUpdateRequest;

import java.net.URI;
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

    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(@Valid @RequestBody SubjectCreateRequest request) {
        Subject subject = subjectMapper.toEntity(request);
        Subject createdSubject = subjectService.create(subject);
        SubjectResponse response = subjectMapper.toResponse(createdSubject);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public SubjectResponse getSubjectById(@PathVariable int id) {
        return subjectMapper.toResponse(subjectService.getById(id));
    }

    @PutMapping("/{id}")
    public SubjectResponse updateSubject(
            @PathVariable int id,
            @Valid @RequestBody SubjectUpdateRequest request
    ) {
        Subject updatedSubject = subjectService.updateName(id, request.name());
        return subjectMapper.toResponse(updatedSubject);
    }
}