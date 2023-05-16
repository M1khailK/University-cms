package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.repository.SubjectRepository;
import ua.foxminded.university.services.SubjectService;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public void save(Subject subject) {
        subjectRepository.save(subject);
    }

    @Override
    public Subject getById(Integer subjectId) {
        return subjectRepository.findById(subjectId).get();
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
