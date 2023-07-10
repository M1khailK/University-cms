package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.generator.PasswordGenerator;
import ua.foxminded.university.services.EmailSenderService;
import ua.foxminded.university.services.PasswordService;

import java.lang.reflect.Array;
import java.nio.CharBuffer;
import java.util.Arrays;

@Service
public class PasswordServiceImpl implements PasswordService {
    @Autowired
    private PasswordGenerator passwordGenerator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailSenderService emailSenderService;

    @Override
    public void generateAndSendPasswordForUser(User user) {
        char[] password = passwordGenerator.generatePassword();
        CharSequence sequencePassword = CharBuffer.wrap(password);
        emailSenderService.sendRegistrationEmail(user, sequencePassword);
        user.setPassword(passwordEncoder.encode(sequencePassword));
        Arrays.fill(password, '\0');
    }

}
