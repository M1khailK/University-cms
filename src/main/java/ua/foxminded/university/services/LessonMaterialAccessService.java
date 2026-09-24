package ua.foxminded.university.services;

import java.util.List;

public interface LessonMaterialAccessService {

    List<Integer> findAccessibleLessonIds(
            String authenticatedEmail,
            String authenticatedRole
    );
}