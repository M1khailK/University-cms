package ua.foxminded.university.services.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.DuplicateEmailException;
import ua.foxminded.university.customexceptions.InvalidOldPasswordException;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.PasswordService;
import ua.foxminded.university.services.StudentService;

import java.nio.CharBuffer;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GroupService groupService;
    @Autowired
    private PasswordService passwordService;

    @Override
    @Transactional
    public void save(Student student) {
        studentRepository.save(student);
    }

    @Override
    public Student getById(int studentId) {
        return studentRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student was not found by id"));
    }

    @Override
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student getByEmail(String email) {
        return studentRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Student was not found by email"));
    }

    @Override
    public List<Lesson> getLessonsByUserIdAndDateBetween(int id, LocalDate from, LocalDate to) {
        Student student = getById(id);
        return lessonService.getAllByGroupAndDateBetween(student.getGroup(), from, to);
    }

    @Override
    @Autowired
    public void register(ServiceManager manager) {
        manager.register("ROLE_STUDENT", this);
    }

    @Override
    public String getPasswordById(int id) {
        return studentRepository.findPasswordById(id).orElseThrow(() -> new IllegalArgumentException("Password was not found by student's id"));
    }


    @Override
    public void changePassword(String email, char[] oldPassword, char[] newPassword) {
        Student student = getByEmail(email);
        String oldPass = getPasswordById(student.getId());
        if (passwordEncoder.matches(CharBuffer.wrap(oldPassword), oldPass)) {
            studentRepository.changePasswordById(passwordEncoder.encode(CharBuffer.wrap(newPassword)), student.getId());
        } else {
            throw new InvalidOldPasswordException("The old password is incorrect!");
        }
        Arrays.fill(oldPassword, '\0');
        Arrays.fill(newPassword, '\0');
    }

    @Override
    public void createUserAccountByRole(User user, String role) {
        try {
            passwordService.generateAndSendPasswordForUser(user);
            Group group = groupService.getByName(user.getGroupName());
            Student student = new Student(null, user.getFirstName(), user.getLastName(), user.getEmail(), group,
                    user.getPassword(), role);
            save(student);
        } catch (
                DataIntegrityViolationException exception) {
            throw new DuplicateEmailException("Email already exists. Please choose a different email.");
        }
    }

}
