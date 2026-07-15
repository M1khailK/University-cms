package ua.foxminded.university.services;

import ua.foxminded.university.info.Subject;

public interface SubjectService extends EntityService<Subject> {

    Subject create(Subject subject);

    Subject updateName(int subjectId, String subjectName);

    void deleteById(int subjectId);

    void changeNameById(int subjectId, String subjectName);
}