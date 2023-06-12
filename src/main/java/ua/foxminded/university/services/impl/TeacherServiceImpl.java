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

    private static final String DUPLICATED_EMAIL_MESSAGE = "Email already exists. Please choose a different email.";
    private static final String PASSWORD_IS_INCORRECT = "The old password is incorrect!";

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
            throw new DuplicateEmailException(DUPLICATED_EMAIL_MESSAGE);
        }
    }

    @Override
    public Teacher getById(int teacherId) {
        return teacherRepository.findById(teacherId).orElseThrow(() -> new IllegalArgumentException("Teacher id was not found"));
    }

    @Override
    public List<Teacher> getAll() {
        return teacherRepository.findAll();
    }

    @Override
    public String getPasswordById(int id) {
        return teacherRepository.findPasswordById(id).orElseThrow(() -> new IllegalArgumentException("Password was not found by teacher's id"));
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        Teacher teacher = getByEmail(email);
        String oldPass = getPasswordById(teacher.getId());
        if (passwordEncoder.matches(oldPassword, oldPass)) {
            teacherRepository.changePasswordById(passwordEncoder.encode(newPassword), teacher.getId());
            teacherRepository.save(teacher);
        } else {
            throw new InvalidOldPasswordException(PASSWORD_IS_INCORRECT);
        }
    }

    @Override
    public String getRole() {
        return "ROLE_TEACHER";
    }

    @Override
    public Teacher getByEmail(String email) {
        return teacherRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Teacher was not found by email"));
    }

    @Override
    public List<Lesson> getLessonsByUserIdAndDateBetween(int id, LocalDate from, LocalDate to) {
        Teacher teacher = getById(id);
        return lessonService.getAllByTeacherAndDateBetween(teacher, from, to);
    }

    @Override
    public List<Teacher> getAllEnabledTeachers() {
        return teacherRepository.findAllEnabledTeachers().orElseThrow(() -> new IllegalArgumentException("All enabled teachers were not found"));
    }

    @Override
    public void saveTeacherAsAdmin(Teacher teacher) {
        try {
            teacherRepository.save(teacher);
            teacherRepository.setAdminRole(teacher.getId());
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException(DUPLICATED_EMAIL_MESSAGE);
        }
    }
}
