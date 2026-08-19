package ua.foxminded.university.api.admin.mapper;

import org.mapstruct.Mapper;
import ua.foxminded.university.api.admin.dto.AdminResponse;
import ua.foxminded.university.info.Teacher;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminResponse toResponse(Teacher admin);
}