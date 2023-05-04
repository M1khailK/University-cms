package ua.foxminded.university.services;

import ua.foxminded.university.info.Teacher;

import java.util.Optional;

public interface TeacherService extends EntityService<Teacher> {
    Optional<Teacher> getByEmail(String email);
}
