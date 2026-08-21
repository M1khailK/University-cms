package ua.foxminded.university.api.profile.mapper;

import org.springframework.stereotype.Component;
import ua.foxminded.university.api.profile.dto.ProfileResponse;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;

@Component
public class ProfileMapper {

    public ProfileResponse toResponse(Object user) {
        if (user instanceof Student student) {
            Group group = student.getGroup();

            return new ProfileResponse(
                    student.getId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getRole(),
                    group == null ? null : group.getId(),
                    group == null ? null : group.getName()
            );
        }

        if (user instanceof Teacher teacher) {
            return new ProfileResponse(
                    teacher.getId(),
                    teacher.getFirstName(),
                    teacher.getLastName(),
                    teacher.getEmail(),
                    teacher.getRole(),
                    null,
                    null
            );
        }

        throw new IllegalArgumentException("Unsupported profile type: " + user.getClass().getName());
    }
}