package ua.foxminded.university.services;

import ua.foxminded.university.info.Subject;

public interface SubjectService extends EntityService<Subject> {

    Subject create(Subject subject);

    void changeNameById(int subjectId, String subjectName);
}