package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.DuplicateEmailException;
import ua.foxminded.university.customexceptions.InvalidOldPasswordException;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;

import java.time.LocalDate;
import java.util.List;


@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void save(Student student) {
        try {
            studentRepository.save(student);
            studentRepository.setStudentRole(student.getId());
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException("Email already exists. Please choose a different email.");
        }
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
    public String getPasswordById(int id) {
        return studentRepository.findPasswordById(id).orElseThrow(() -> new IllegalArgumentException("Password was not found by student's id"));
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        Student student = getByEmail(email);
        String oldPass = getPasswordById(student.getId());
        if (passwordEncoder.matches(oldPassword, oldPass)) {
            studentRepository.changePasswordById(passwordEncoder.encode(newPassword), student.getId());
            studentRepository.save(student);
        } else {
            throw new InvalidOldPasswordException("The old password is incorrect!");
        }
    }

    @Override
    public String getRole() {
        return "ROLE_STUDENT";
    }

    @Override
    public List<Student> getAllEnabledStudents() {
        return studentRepository.findAllEnabledStudents().orElseThrow(() -> new IllegalArgumentException("All enabled students were not found"));
    }
}
