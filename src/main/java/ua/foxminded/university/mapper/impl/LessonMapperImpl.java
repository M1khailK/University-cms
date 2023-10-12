package ua.foxminded.university.mapper.impl;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ua.foxminded.university.dto.LessonDTO;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.mapper.LessonMapper;

@Mapper(componentModel = "spring")
@Component
public class LessonMapperImpl implements LessonMapper {
    @Override
    public Lesson toLesson(LessonDTO lessonDTO) {
        return new Lesson(null, lessonDTO.getName(), lessonDTO.getDate(),
                lessonDTO.getStartTime(),lessonDTO.getEndTime(),
                lessonDTO.getSubject(),lessonDTO.getGroup(),lessonDTO.getTeacher());
    }
}
