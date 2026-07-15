package ua.foxminded.university.api.subject.mapper;

import org.mapstruct.Mapper;
import ua.foxminded.university.api.subject.dto.SubjectResponse;
import ua.foxminded.university.info.Subject;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    SubjectResponse toResponse(Subject subject);
}