package ua.foxminded.university.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.SubjectNotFoundException;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.repository.SubjectRepository;
import ua.foxminded.university.services.SubjectService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Override
    public Subject create(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Override
    public Subject updateName(int subjectId, String subjectName) {
        Subject subject = getById(subjectId);
        subject.setName(subjectName);
        return subjectRepository.save(subject);
    }

    @Override
    public void save(Subject subject) {
        subjectRepository.save(subject);
    }

    @Override
    public Subject getById(int subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));
    }

    @Override
    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    @Override
    public void deleteById(int subjectId) {
        Subject subject = getById(subjectId);
        subjectRepository.delete(subject);
    }
}