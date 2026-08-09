package ua.foxminded.university.api.lesson.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.foxminded.university.api.lesson.dto.LessonCreateRequest;
import ua.foxminded.university.api.lesson.dto.LessonResponse;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.api.lesson.dto.LessonUpdateRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonApiMapper {

    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "subject.name", target = "subjectName")
    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.name", target = "groupName")
    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "teacher.firstName", target = "teacherFirstName")
    @Mapping(source = "teacher.lastName", target = "teacherLastName")
    @Mapping(source = "teacher.email", target = "teacherEmail")
    LessonResponse toResponse(Lesson lesson);

    List<LessonResponse> toResponses(List<Lesson> lessons);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Lesson toEntity(LessonCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Lesson toEntity(LessonUpdateRequest request);
}
