package ua.foxminded.university.services;

import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.manager.ServiceManager;

import java.time.LocalDate;
import java.util.List;

public interface UserManagerService<T> {
    void changePassword(String email, String oldPassword, String newPassword);

    T getByEmail(String email);

    List<Lesson> getLessonsByUserIdAndDateBetween(int id, LocalDate from, LocalDate to);

    void register(ServiceManager manager);

}
