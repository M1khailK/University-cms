package ua.foxminded.university.services;

import jakarta.mail.MessagingException;

public interface EmailSenderService {

    void sendEmail(String to, String subject) throws MessagingException;
}
