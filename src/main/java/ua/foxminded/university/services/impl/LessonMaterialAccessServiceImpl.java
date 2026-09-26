package ua.foxminded.university.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.services
        .LessonMaterialAccessService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonMaterialAccessServiceImpl
        implements LessonMaterialAccessService {

    private static final String STUDENT_ROLE = "ROLE_STUDENT";
    private static final String TEACHER_ROLE = "ROLE_TEACHER";
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    private final LessonRepository lessonRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Integer> findAccessibleLessonIds(
            String authenticatedEmail,
            String authenticatedRole
    ) {
        String email = validateEmail(authenticatedEmail);
        String role = validateRole(authenticatedRole);

        List<Integer> lessonIds = switch (role) {
            case STUDENT_ROLE ->
                    lessonRepository
                            .findIdsAccessibleToStudent(email);

            case TEACHER_ROLE ->
                    lessonRepository
                            .findIdsAccessibleToTeacher(email);

            case ADMIN_ROLE ->
                    lessonRepository.findAllIds();

            default -> throw new AccessDeniedException(
                    "The authenticated role cannot access lesson materials."
            );
        };

        return List.copyOf(lessonIds);
    }

    private String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Authenticated email must not be blank."
            );
        }

        return email.strip();
    }

    private String validateRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException(
                    "Authenticated role must not be blank."
            );
        }

        return role.strip();
    }
}