package ua.foxminded.university.services.search;

import ua.foxminded.university.services.search.model
        .LessonMaterialSearchResult;

import java.util.List;

public interface LessonMaterialSemanticSearchService {

    List<LessonMaterialSearchResult> search(
            String query,
            String authenticatedEmail,
            String authenticatedRole,
            int limit
    );
}