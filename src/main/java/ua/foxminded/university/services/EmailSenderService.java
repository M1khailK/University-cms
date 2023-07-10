package ua.foxminded.university.services;

import ua.foxminded.university.customexceptions.MailSenderServiceException;
import ua.foxminded.university.dto.User;

import java.util.Map;

public interface EmailSenderService {

    void sendEmail(String to, String subject, String emailTemplate, Map<String, Object> templateParams)
            throws MailSenderServiceException;

    void sendRegistrationEmail(User user,CharSequence password);
}
