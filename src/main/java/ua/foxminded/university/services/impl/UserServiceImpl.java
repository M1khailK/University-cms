package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.InvalidUserIdException;
import ua.foxminded.university.repository.UserRepository;
import ua.foxminded.university.services.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void disableUserById(Integer id) {
        if (id == null) {
            throw new InvalidUserIdException("The user id is incorrect!!");
        }
        userRepository.deactivateUserAccountById(id);
    }
}
