package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.DuplicateEmailException;
import ua.foxminded.university.customexceptions.InvalidOldPasswordException;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.TeacherRepository;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDate;
import java.util.List;


@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void save(Teacher teacher) {
        try {
            teacherRepository.save(teacher);
            teacherRepository.setTeacherRole(teacher.getId());
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException("Email already exists. Please choose a different email.");
        }
    }

    @Override
    public Teacher getById(Integer teacherId) {
        return teacherRepository.findById(teacherId).get();
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
        return "ROLE_TEACHER";
    }

    @Override
    public Teacher getByEmail(String email) {
        return teacherRepository.findByEmail(email).get();
    }

    @Override
    public List<Lesson> getLessonsByUserIdAndDateBetween(int id, LocalDate from, LocalDate to) {
        Teacher teacher = teacherRepository.findById(id).get();
        return lessonService.getAllByTeacherAndDateBetween(teacher, from, to);
    }

    @Override
    public List<Teacher> getAllEnabledTeachers() {
        return teacherRepository.findAllEnabledTeachers();
    }

    @Override
    public void saveTeacherAsAdmin(Teacher teacher) {
        teacherRepository.save(teacher);
        teacherRepository.setAdminRole(teacher.getId());
    }
}
