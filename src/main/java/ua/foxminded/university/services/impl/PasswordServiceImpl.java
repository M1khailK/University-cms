package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.generator.PasswordGenerator;
import ua.foxminded.university.services.EmailSenderService;
import ua.foxminded.university.services.PasswordService;

import java.nio.CharBuffer;

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
        CharSequence password = CharBuffer.wrap(passwordGenerator.generatePassword());
        emailSenderService.sendRegistrationEmail(user,password);
        user.setPassword(passwordEncoder.encode(password));
    }
}
