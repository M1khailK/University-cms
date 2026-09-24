package ua.foxminded.university.services.assistant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.beans.factory.ObjectProvider;
import ua.foxminded.university.services.search
        .LessonMaterialSemanticSearchService;
import ua.foxminded.university.services.search.model
        .LessonMaterialSearchResult;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonMaterialAssistantToolsTest {

    @Mock
    private ObjectProvider<LessonMaterialSemanticSearchService>
            searchServiceProvider;

    @Mock
    private LessonMaterialSemanticSearchService searchService;

    @Test
    void searchLessonMaterials_shouldReturnAccessibleExcerpts() {
        when(searchServiceProvider.getIfAvailable())
                .thenReturn(searchService);

        when(searchService.search(
                "What is transaction isolation?",
                "student@university.com",
                "ROLE_STUDENT",
                5
        )).thenReturn(List.of(
                new LessonMaterialSearchResult(
                        100L,
                        20,
                        10,
                        "transactions.pdf",
                        4,
                        2,
                        "Isolation separates concurrent transactions.",
                        0.12
                )
        ));

        LessonMaterialAssistantTools tools =
                new LessonMaterialAssistantTools(
                        searchServiceProvider
                );

        LessonMaterialAssistantTools.SearchResult result =
                tools.searchLessonMaterials(
                        "What is transaction isolation?",
                        studentToolContext()
                );

        assertTrue(result.successful());
        assertEquals(null, result.message());
        assertEquals(1, result.excerpts().size());

        LessonMaterialAssistantTools.MaterialExcerpt excerpt =
                result.excerpts().getFirst();

        assertEquals(10, excerpt.lessonId());
        assertEquals(20, excerpt.materialId());
        assertEquals(
                "transactions.pdf",
                excerpt.filename()
        );
        assertEquals(4, excerpt.pageNumber());
        assertEquals(2, excerpt.chunkIndex());
        assertEquals(
                "Isolation separates concurrent transactions.",
                excerpt.text()
        );

        verify(searchService).search(
                "What is transaction isolation?",
                "student@university.com",
                "ROLE_STUDENT",
                5
        );
    }

    @Test
    void searchLessonMaterials_shouldReturnNoResultsMessage() {
        when(searchServiceProvider.getIfAvailable())
                .thenReturn(searchService);

        when(searchService.search(
                "Unknown topic",
                "student@university.com",
                "ROLE_STUDENT",
                5
        )).thenReturn(List.of());

        LessonMaterialAssistantTools tools =
                new LessonMaterialAssistantTools(
                        searchServiceProvider
                );

        LessonMaterialAssistantTools.SearchResult result =
                tools.searchLessonMaterials(
                        "Unknown topic",
                        studentToolContext()
                );

        assertTrue(result.successful());

        assertEquals(
                "No relevant lesson material excerpts were found.",
                result.message()
        );

        assertEquals(List.of(), result.excerpts());
    }

    @Test
    void searchLessonMaterials_shouldReportUnavailableSearch() {
        when(searchServiceProvider.getIfAvailable())
                .thenReturn(null);

        LessonMaterialAssistantTools tools =
                new LessonMaterialAssistantTools(
                        searchServiceProvider
                );

        LessonMaterialAssistantTools.SearchResult result =
                tools.searchLessonMaterials(
                        "What is transaction isolation?",
                        studentToolContext()
                );

        assertFalse(result.successful());

        assertEquals(
                "Lesson material search is not configured.",
                result.message()
        );

        assertEquals(List.of(), result.excerpts());

        verifyNoInteractions(searchService);
    }

    @Test
    void searchLessonMaterials_shouldRejectMissingTrustedContext() {
        LessonMaterialAssistantTools tools =
                new LessonMaterialAssistantTools(
                        searchServiceProvider
                );

        ToolContext emptyContext =
                new ToolContext(Map.of());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> tools.searchLessonMaterials(
                        "What is transaction isolation?",
                        emptyContext
                )
        );

        assertEquals(
                "Authenticated assistant context is missing.",
                exception.getMessage()
        );

        verifyNoInteractions(
                searchServiceProvider,
                searchService
        );
    }

    private ToolContext studentToolContext() {
        return new ToolContext(Map.of(
                UniversityAssistantTools.ASSISTANT_CONTEXT_KEY,
                new AssistantToolContext(
                        "student@university.com",
                        "ROLE_STUDENT"
                )
        ));
    }
}