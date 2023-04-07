package ua.foxminded.university.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.foxminded.university.repository.GroupRepository;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.repository.SubjectRepository;
import ua.foxminded.university.repository.TeacherRepository;

@SpringBootConfiguration
public class RepositoryMockBeanConfig {


    @MockBean
    private TeacherRepository teacherRepository;

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private SubjectRepository subjectRepository;

}
