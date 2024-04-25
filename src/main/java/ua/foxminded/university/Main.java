package ua.foxminded.university;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ua.foxminded.university.dto.LessonDTO;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.mapper.LessonMapper;
import ua.foxminded.university.mapper.LessonMapperImpl;


@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);
    }
}
