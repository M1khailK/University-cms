package ua.foxminded.university.services;

import ua.foxminded.university.info.Lesson;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    List<Lesson> getUserLessons(LocalDate dateFrom, LocalDate dateTo);

    int getUserIdByEmail(String email);

    void disableUserById(int id);

}
