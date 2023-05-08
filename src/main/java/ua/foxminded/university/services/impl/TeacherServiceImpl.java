package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.TeacherRepository;
import ua.foxminded.university.services.TeacherService;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
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
    public Optional<Teacher> getByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    @Override
    public String getPasswordById(int id) {
        return teacherRepository.findPasswordById(id);
    }

    @Override
    public void changePasswordById(String newPassword, int id) {
        teacherRepository.changePasswordById(newPassword, id);
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
