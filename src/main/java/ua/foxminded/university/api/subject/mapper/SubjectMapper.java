package ua.foxminded.university.api.subject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.foxminded.university.api.subject.dto.SubjectCreateRequest;
import ua.foxminded.university.api.subject.dto.SubjectResponse;
import ua.foxminded.university.info.Subject;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    SubjectResponse toResponse(Subject subject);

    @Mapping(target = "id", ignore = true)
    Subject toEntity(SubjectCreateRequest request);
}