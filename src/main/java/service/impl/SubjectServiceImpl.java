package service.impl;

import info.Subject;
import repository.SubjectRepository;
import service.SubjectService;

import java.util.List;
import java.util.Optional;

public class SubjectServiceImpl implements SubjectService {

    private SubjectRepository subjectRepository;

    @Override
    public void save(Subject subject) {
        subjectRepository.save(subject);
    }

    @Override
    public Optional<Subject> getById(Integer subjectId) {
        return subjectRepository.findById(subjectId);
    }

    @Override
    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    @Override
    public void deleteById(Integer subjectId) {
        subjectRepository.deleteById(subjectId);
    }
}
