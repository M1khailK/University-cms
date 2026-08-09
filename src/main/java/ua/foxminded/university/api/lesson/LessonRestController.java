package ua.foxminded.university.api.lesson;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.lesson.dto.LessonCreateRequest;
import ua.foxminded.university.api.lesson.dto.LessonResponse;
import ua.foxminded.university.api.lesson.mapper.LessonApiMapper;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonRestController {

    private final LessonService lessonService;
    private final LessonApiMapper lessonMapper;
    private final SubjectService subjectService;
    private final GroupService groupService;
    private final TeacherService teacherService;

    @GetMapping
    public List<LessonResponse> getAllLessons() {
        return lessonMapper.toResponses(lessonService.getAll());
    }

    @GetMapping("/{id}")
    public LessonResponse getLessonById(@PathVariable int id) {
        return lessonMapper.toResponse(lessonService.getById(id));
    }

    @PostMapping
    public ResponseEntity<LessonResponse> createLesson(@Valid @RequestBody LessonCreateRequest request) {
        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setSubject(subjectService.getById(request.subjectId()));
        lesson.setGroup(groupService.getById(request.groupId()));
        lesson.setTeacher(teacherService.getById(request.teacherId()));

        Lesson createdLesson = lessonService.create(lesson);

        return ResponseEntity
                .created(URI.create("/api/v1/lessons/" + createdLesson.getId()))
                .body(lessonMapper.toResponse(createdLesson));
    }
}
