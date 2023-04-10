package ua.foxminded.university.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ua.foxminded.university.services.EmailSenderService;

import java.nio.charset.StandardCharsets;

@Service
@PropertySource("classpath:mailTemplate.properties")
public class EmailSenderServiceImpl implements EmailSenderService {

    @Value("${mail.template}")
    private String mailTemplate;

    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String toEmail, String subject) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();

        String html = templateEngine.process(mailTemplate, context);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(mimeMessage);
    }
}