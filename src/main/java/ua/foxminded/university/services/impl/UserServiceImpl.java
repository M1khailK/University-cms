package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.InvalidUserIdException;
import ua.foxminded.university.repository.UserRepository;
import ua.foxminded.university.services.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;

    @Override
    public Integer getUserIdByEmail(String email) {
        return userRepository.findUserIdByEmail(email);
    }

    @Override
    public void disableUserById(Integer id) {
        if (id == null) {
            throw new InvalidUserIdException("The user id is incorrect!!");
        }
        userRepository.deactivateUserAccountById(id);
    }

    @Override
    public void insertUser(String firstName, String lastName, String email, String password) {
        userRepository.insertUser(firstName, lastName, email, encoder.encode(password));
    }

    @Override
    public void insertStudentById(Integer userId, Integer groupId) {
        userRepository.insertStudent(userId, groupId);
    }

    @Override
    public void insertTeacherById(Integer userId) {
        userRepository.insertTeacher(userId);
    }

    @Override
    public void insertAdmin(Integer userId) {
        userRepository.insertAdmin(userId);
    }

}
