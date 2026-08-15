package ua.foxminded.university.api.teacher.mapper;

import org.mapstruct.Mapper;
import ua.foxminded.university.api.teacher.dto.TeacherResponse;
import ua.foxminded.university.info.Teacher;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    TeacherResponse toResponse(Teacher teacher);

    List<TeacherResponse> toResponses(List<Teacher> teachers);
}
