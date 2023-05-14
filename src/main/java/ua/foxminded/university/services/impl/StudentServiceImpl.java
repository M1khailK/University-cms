package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.InvalidOldPasswordException;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


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
        studentRepository.save(student);
    }

    @Override
    public Optional<Student> getById(Integer studentId) {
        return studentRepository.findById(studentId);
    }

    @Override
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @Override
    public void deleteById(Integer studentId) {
        studentRepository.deleteById(studentId);
    }

    @Override
    public Student getByEmail(String email) {
        return studentRepository.findByEmail(email).get();
    }

    @Override
    public Integer getUserIdByEmail(String email) {
        return studentRepository.findIdByEmail(email);
    }

    @Override
    public List<Lesson> getLessonsByUserIdAndDateBetween(int id, LocalDate from, LocalDate to) {
        Student student = studentRepository.findStudentByUserId(id);
        return lessonService.getAllByGroupAndDateBetween(student.getGroup(), from, to);
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        Student student = studentRepository.findByEmail(email).get();
        String oldPass = studentRepository.findPasswordById(student.getId());
        if (passwordEncoder.matches(oldPassword, oldPass)) {
            studentRepository.changePasswordById(passwordEncoder.encode(newPassword), student.getId());
            studentRepository.save(student);
        } else {
            throw new InvalidOldPasswordException("The old password is incorrect!");
        }
    }

    @Override
    public String getRole() {
        return "[ROLE_STUDENT]";
    }
}
