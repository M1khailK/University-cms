package ua.foxminded.university.mapper;

import org.mapstruct.Mapper;
import ua.foxminded.university.dto.LessonDTO;
import ua.foxminded.university.info.Lesson;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    Lesson toLesson(LessonDTO lessonDTO);
}
