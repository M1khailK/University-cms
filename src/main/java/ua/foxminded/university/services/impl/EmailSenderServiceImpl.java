package ua.foxminded.university.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import ua.foxminded.university.customexceptions.MailSenderServiceException;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.services.EmailSenderService;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

    private static final String SUBJECT = "User password";

    private final JavaMailSender mailSender;

    @Override
    public void sendRegistrationEmail(User user, CharSequence password) {
        String html = buildRegistrationEmailBody(user, password);

        sendHtmlEmail(user.getEmail(), SUBJECT, html);
    }

    private void sendHtmlEmail(String toEmail, String subject, String html) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new MailSenderServiceException("Error sending email", exception);
        }
    }

    private String buildRegistrationEmailBody(User user, CharSequence password) {
        String firstName = HtmlUtils.htmlEscape(user.getFirstName());
        String lastName = HtmlUtils.htmlEscape(user.getLastName());
        String escapedPassword = HtmlUtils.htmlEscape(password.toString());

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>USER PASSWORD</title>
                </head>
                <body>
                    <h1>Password for the User</h1>
                    <p>Hello, %s %s!</p>
                    <p>Your password for accessing the system: <strong>%s</strong></p>
                    <p>Best regards,</p>
                    <p>your university platform</p>
                </body>
                </html>
                """.formatted(firstName, lastName, escapedPassword);
    }
}