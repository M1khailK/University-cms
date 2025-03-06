package ua.foxminded.university.services;

import ua.foxminded.university.dto.User;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.manager.ServiceManager;

import java.time.LocalDate;
import java.util.List;

public interface UserManagerService<T> {
    void changePassword(String email, char[] oldPassword, char[] newPassword);

    void createUserAccountByRole(User user, String role);

    T getByEmail(String email);

    List<Lesson> getLessonsByUserIdAndDateBetween(int id, LocalDate from, LocalDate to);

    void register(ServiceManager manager);

}
