package ua.foxminded.university.api.group.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.foxminded.university.api.group.dto.GroupCreateRequest;
import ua.foxminded.university.api.group.dto.GroupResponse;
import ua.foxminded.university.info.Group;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    GroupResponse toResponse(Group group);

    List<GroupResponse> toResponse(List<Group> groups);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    Group toEntity(GroupCreateRequest request);
}
