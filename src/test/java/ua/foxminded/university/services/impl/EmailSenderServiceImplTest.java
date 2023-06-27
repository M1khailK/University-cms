package ua.foxminded.university.services.impl;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import ua.foxminded.university.config.service.ServicesTestConfig;
import ua.foxminded.university.customexceptions.MailSenderServiceException;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.repository.TeacherRepository;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest()
@MockBean(LessonRepository.class)
@MockBean(TeacherRepository.class)
@MockBean(StudentRepository.class)
@MockBean(Clock.class)
@MockBean(PasswordEncoder.class)
@ContextConfiguration(classes = ServicesTestConfig.class)
public class EmailSenderServiceImplTest {

    private static final String EMAIL = "mailsenderexample515@gmail.com";
    private static final String SUBJECT = "test subject";
    private static final String EXAMPLE = "example";

    @Autowired
    private EmailSenderServiceImpl emailSender;

    private static final GreenMail greenMail = new GreenMail(new ServerSetup(25, "localhost", "smtp"))
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("admin", "password"));

    @BeforeEach
    public void setUp() {
        greenMail.start();
    }

    @AfterEach
    public void teardown() {
        greenMail.stop();
    }

    @Test
    void emailSender_shouldSendEmail_whenInputHasBodySubjectAndRecipientEmail() throws MailSenderServiceException, MessagingException {
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(EXAMPLE, EXAMPLE);

        emailSender.sendEmail(EMAIL, SUBJECT, EXAMPLE, templateParams);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        Assertions.assertEquals(1, receivedMessages.length);
        MimeMessage currentMessage = receivedMessages[0];
        Assertions.assertEquals(SUBJECT, currentMessage.getSubject());
        Assertions.assertEquals(EMAIL, currentMessage.getAllRecipients()[0].toString());

    }
}