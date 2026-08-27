package ua.foxminded.university.services;

import ua.foxminded.university.dto.User;

public interface EmailSenderService {

    void sendRegistrationEmail(User user,CharSequence password);
}
