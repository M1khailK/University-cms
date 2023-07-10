package ua.foxminded.university.services.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.InvalidDateRangeException;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.repository.UserRepository;
import ua.foxminded.university.services.UserManagerService;
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private ServiceManager serviceManager;
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

    @Override
    @Transactional
    public List<Lesson> getUserLessons(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null && dateTo != null) {
            throw new InvalidDateRangeException("From date cannot be null when To date is provided.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int id = getUserIdByEmail(authentication.getName());
        UserManagerService service = serviceManager.getUserManagerServiceByAuthentication();
        return service.getLessonsByUserIdAndDateBetween(id, dateFrom, dateTo);
    }
}
