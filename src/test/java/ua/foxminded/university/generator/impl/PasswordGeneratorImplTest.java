package ua.foxminded.university.generator.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ua.foxminded.university.config.generator.PasswordGeneratorTestConfig;
import ua.foxminded.university.generator.PasswordGenerator;

@SpringBootTest
@ContextConfiguration(classes = PasswordGeneratorTestConfig.class)
public class PasswordGeneratorImplTest {

    @Autowired
    private PasswordGenerator passwordGenerator;

    @RepeatedTest(5)
    public void passwordGenerator_shouldMatchRegex_whenPasswordIsGenerated() {
        String passwordPattern = "^[A-Za-z0-9]{8}$";
        String password = new String(passwordGenerator.generatePassword());
        Assertions.assertTrue(password.matches(passwordPattern));

    }
}
