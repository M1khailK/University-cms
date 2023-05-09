package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.InvalidDateRangeException;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.services.StudentService;

import java.util.List;
import java.util.Optional;


@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;
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
    public void changePassword(String email, String oldPassword, String newPassword) {
        Student student = studentRepository.findByEmail(email).get();
        String oldPass = studentRepository.findPasswordById(student.getId());
        if (passwordEncoder.matches(oldPassword, oldPass)) {
            student.setPassword(newPassword);
            studentRepository.save(student);
        } else {
            throw new InvalidDateRangeException("");
        }
    }

    @Override
    public String getRole() {
        return "[ROLE_STUDENT]";
    }

    @Override
    public Student getByEmail(String email) {
        return studentRepository.findByEmail(email).get();
    }

}
