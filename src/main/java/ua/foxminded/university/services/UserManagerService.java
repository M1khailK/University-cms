package ua.foxminded.university.services;

import ua.foxminded.university.info.Lesson;

import java.time.LocalDate;
import java.util.List;

public interface UserManagerService<T> {
    void changePassword(String email, String oldPassword, String newPassword);

    String getRole();

    T getByEmail(String email);

    Integer getUserIdByEmail(String email);

    List<Lesson> getLessonsByUserIdAndDateBetween(int id, LocalDate from, LocalDate to);
}
