package service.impl;

import info.Student;
import repository.StudentRepository;
import service.Service;

import java.util.List;
import java.util.Optional;

public class StudentServiceImpl implements Service<Student> {

    private StudentRepository studentRepository;

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
}
