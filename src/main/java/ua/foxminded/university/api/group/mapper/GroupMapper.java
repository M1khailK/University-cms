package ua.foxminded.university.api.group.mapper;

import org.mapstruct.Mapper;
import ua.foxminded.university.api.group.dto.GroupResponse;
import ua.foxminded.university.info.Group;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    GroupResponse toResponses(Group group);

    List<GroupResponse> toResponses(List<Group> groups);
}
