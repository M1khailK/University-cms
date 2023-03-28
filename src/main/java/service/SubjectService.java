package service;

import info.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectService {

    void save(Subject subject);

    Optional<Subject> getById(Integer subjectId);

    List<Subject> getAll();

    void deleteById(Integer subjectId);

}
