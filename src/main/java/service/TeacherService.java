package service;

import info.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherService {

    void save(Teacher teacher);

    Optional<Teacher> getById(Integer teacherId);

    List<Teacher> getAll();

    void deleteById(Integer teacherId);

}
