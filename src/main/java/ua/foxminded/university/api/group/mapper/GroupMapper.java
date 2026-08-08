package ua.foxminded.university.api.group.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.foxminded.university.api.group.dto.GroupCreateRequest;
import ua.foxminded.university.api.group.dto.GroupResponse;
import ua.foxminded.university.api.group.dto.GroupStudentResponse;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    GroupResponse toResponse(Group group);

    List<GroupResponse> toResponses(List<Group> groups);

    GroupStudentResponse toStudentResponse(Student student);

    List<GroupStudentResponse> toStudentResponses(List<Student> students);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    Group toEntity(GroupCreateRequest request);


}
