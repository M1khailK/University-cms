package service;

import info.Lesson;
import info.Student;
import info.Subject;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    void save(Student student);

    Optional<Student> getById(Integer studentId);

    List<Student> getAll();

    void deleteById(Integer studentId);

}
