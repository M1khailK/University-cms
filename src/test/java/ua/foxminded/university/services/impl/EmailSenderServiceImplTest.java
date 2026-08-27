package ua.foxminded.university.services.impl;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import ua.foxminded.university.config.service.ServicesTestConfig;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.repository.TeacherRepository;
import jakarta.mail.Multipart;
import jakarta.mail.Part;

import java.io.IOException;
import java.io.IOException;
import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@MockBean(LessonRepository.class)
@MockBean(TeacherRepository.class)
@MockBean(StudentRepository.class)
@MockBean(Clock.class)
@MockBean(PasswordEncoder.class)
@ContextConfiguration(classes = ServicesTestConfig.class)
class EmailSenderServiceImplTest {

    private static final String EMAIL = "mailsenderexample515@gmail.com";
    private static final String PASSWORD = "secret123";

    @Autowired
    private EmailSenderServiceImpl emailSender;

    private static final GreenMail greenMail =
            new GreenMail(new ServerSetup(25, "localhost", "smtp"))
                    .withConfiguration(
                            GreenMailConfiguration.aConfig()
                                    .withUser("admin", "password")
                    );

    @BeforeEach
    void setUp() {
        greenMail.start();
    }

    @AfterEach
    void tearDown() {
        greenMail.stop();
    }

    @Test
    void emailSender_shouldSendRegistrationEmail_whenUserAndPasswordProvided()
            throws MessagingException, IOException {


        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Brown");
        user.setEmail(EMAIL);

        emailSender.sendRegistrationEmail(user, PASSWORD);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];

        assertEquals("User password", message.getSubject());
        assertEquals(EMAIL, message.getAllRecipients()[0].toString());

        String body = extractText(message);

        assertTrue(body.contains("Alice Brown"));
        assertTrue(body.contains(PASSWORD));
    }

    private String extractText(Part part) throws MessagingException, IOException {
        if (part.isMimeType("text/*")) {
            return part.getContent().toString();
        }

        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            StringBuilder content = new StringBuilder();

            for (int i = 0; i < multipart.getCount(); i++) {
                content.append(extractText(multipart.getBodyPart(i)));
            }

            return content.toString();
        }

        return "";
    }
}