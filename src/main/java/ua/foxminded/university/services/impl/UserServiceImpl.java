package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.repository.UserRepository;
import ua.foxminded.university.services.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;

    @Override
    public int getUserIdByEmail(String email) {
        return userRepository.findUserIdByEmail(email).orElseThrow(() -> new IllegalArgumentException("User was not found by id"));
    }

    @Override
    public void disableUserById(int id) {
        userRepository.deactivateUserAccountById(id);
    }

}
