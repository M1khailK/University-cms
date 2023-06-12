package ua.foxminded.university.generator.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.foxminded.university.generator.PasswordGenerator;

@SpringBootTest
public class PasswordGeneratorImplTest {

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Test
    public void passwordGenerator_shouldMatchRegex_whenPasswordIsGenerated() {
        String passwordPattern = "^[A-Za-z0-9]{8}$";
        String password = passwordGenerator.generatePassword();
        Assertions.assertTrue(password.matches(passwordPattern));

    }
}
