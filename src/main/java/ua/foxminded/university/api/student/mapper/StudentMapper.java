package ua.foxminded.university.api.student.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.foxminded.university.api.student.dto.StudentResponse;
import ua.foxminded.university.info.Student;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.name", target = "groupName")
    StudentResponse toResponse(Student student);

    List<StudentResponse> toResponses(List<Student> students);
}
