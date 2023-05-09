package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.InvalidOldPasswordException;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.TeacherRepository;
import ua.foxminded.university.services.TeacherService;

import java.util.List;
import java.util.Optional;


@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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


    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        Teacher teacher = teacherRepository.findByEmail(email).get();
        String oldPass = teacherRepository.findPasswordById(teacher.getId());
        if (passwordEncoder.matches(oldPassword, oldPass)) {
            teacherRepository.changePasswordById(passwordEncoder.encode(newPassword), teacher.getId());
            teacherRepository.save(teacher);
        } else {
            throw new InvalidOldPasswordException("The old password is incorrect!");
        }
    }

    @Override
    public String getRole() {
        return "[ROLE_TEACHER]";
    }

    @Override
    public Teacher getByEmail(String email) {
        return teacherRepository.findByEmail(email).get();
    }

}
