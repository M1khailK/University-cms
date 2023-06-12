package ua.foxminded.university.generator.impl;

import org.springframework.stereotype.Component;
import ua.foxminded.university.generator.PasswordGenerator;

import java.security.SecureRandom;

@Component
public class PasswordGeneratorImpl implements PasswordGenerator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    public String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(CHARS.length());
            stringBuilder.append(CHARS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }
}
