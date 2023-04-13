package ua.foxminded.university.services;

import jakarta.mail.MessagingException;
import ua.foxminded.university.customexceptions.MailSenderServiceException;

import java.util.Map;

public interface EmailSenderService {

    void sendEmail(String to, String subject, String emailTemplate, Map<String, Object> templateParams) throws MailSenderServiceException;
}
