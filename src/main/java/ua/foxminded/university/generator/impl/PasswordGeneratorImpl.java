package ua.foxminded.university.generator.impl;

import org.springframework.stereotype.Component;
import ua.foxminded.university.generator.PasswordGenerator;

import java.security.SecureRandom;

@Component
public class PasswordGeneratorImpl implements PasswordGenerator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    public char[] generatePassword() {
        SecureRandom random = new SecureRandom();
        char[] password = new char[8];
        for (int i = 0; i < password.length; ++i) {
            password[i] = CHARS.charAt(random.nextInt(CHARS.length()));
        }
        return password;
    }
}
