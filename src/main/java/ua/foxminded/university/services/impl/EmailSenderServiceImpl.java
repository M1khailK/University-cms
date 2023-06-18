package ua.foxminded.university.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ua.foxminded.university.customexceptions.MailSenderServiceException;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.services.EmailSenderService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    private static final String EMAIL_TEMPLATE = "userPassword";
    private static final String SUBJECT = "User password";
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String toEmail, String subject, String emailType, Map<String, Object> templateParams) throws MailSenderServiceException {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(templateParams);

            String html = templateEngine.process(emailType, context);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new MailSenderServiceException("Error sending email", exception);
        }
    }

    @Override
    public void sendRegistrationEmail(User user, CharSequence password) {
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("name", user.getFirstName());
        templateParams.put("surname", user.getLastName());
        templateParams.put("password", password);

        sendEmail(user.getEmail(), SUBJECT, EMAIL_TEMPLATE, templateParams);
    }

}