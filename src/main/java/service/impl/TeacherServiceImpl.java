package service.impl;

import info.Teacher;
import repository.TeacherRepository;
import service.TeacherService;

import java.util.List;
import java.util.Optional;

public class TeacherServiceImpl implements TeacherService {

    private TeacherRepository teacherRepository;

    @Override
    public void save(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    @Override
    public Optional<Teacher> getById(Integer teacherId) {
        return teacherRepository.findById(teacherId);
    }

    @Override
    public List<Teacher> getAll() {
        return teacherRepository.findAll();
    }

    @Override
    public void deleteById(Integer teacherId) {
        teacherRepository.deleteById(teacherId);
    }
}
