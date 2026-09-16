package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.repository.UserRepository;
import ua.foxminded.university.services.UserManagerService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private ServiceManager serviceManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserManagerService userManagerService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserLessons_shouldUseExplicitAuthenticatedIdentity() {
        String email = "student@university.com";
        String role = "ROLE_STUDENT";
        LocalDate from = LocalDate.of(2026, 9, 17);
        LocalDate to = LocalDate.of(2026, 9, 18);

        Lesson lesson = new Lesson();

        when(userRepository.findUserIdByEmail(email))
                .thenReturn(Optional.of(7));

        when(serviceManager.getServiceByRole(role))
                .thenReturn(Optional.of(userManagerService));

        when(userManagerService.getLessonsByUserIdAndDateBetween(
                7,
                from,
                to
        )).thenReturn(List.of(lesson));

        List<Lesson> result = userService.getUserLessons(
                email,
                role,
                from,
                to
        );

        assertEquals(List.of(lesson), result);

        verify(userRepository)
                .findUserIdByEmail(email);

        verify(serviceManager)
                .getServiceByRole(role);

        verify(userManagerService)
                .getLessonsByUserIdAndDateBetween(
                        7,
                        from,
                        to
                );
    }
}