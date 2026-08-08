package ua.foxminded.university.api.lesson;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.lesson.dto.LessonResponse;
import ua.foxminded.university.api.lesson.mapper.LessonApiMapper;
import ua.foxminded.university.services.LessonService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonRestController {

    private final LessonService lessonService;
    private final LessonApiMapper lessonMapper;

    @GetMapping
    public List<LessonResponse> getAllLessons(){
        return lessonMapper.toResponses(lessonService.getAll());
    }
    @GetMapping("/{id}")
    public LessonResponse getLessonById(@PathVariable int id){
        return lessonMapper.toResponse(lessonService.getById(id));
    }
}
