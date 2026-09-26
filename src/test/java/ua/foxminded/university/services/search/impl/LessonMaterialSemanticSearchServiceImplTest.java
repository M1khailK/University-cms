package ua.foxminded.university.services.search.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.embedding.EmbeddingModel;
import ua.foxminded.university.repository
        .LessonMaterialChunkSearchRepository;
import ua.foxminded.university.repository.model
        .LessonMaterialChunkSearchResult;
import ua.foxminded.university.services
        .LessonMaterialAccessService;
import ua.foxminded.university.services.search.config.LessonMaterialSearchProperties;
import ua.foxminded.university.services.search.model
        .LessonMaterialSearchResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonMaterialSemanticSearchServiceImplTest {

    private static final double MAX_DISTANCE = 0.45;
    private static final int EXPECTED_DIMENSIONS = 3;

    @Mock
    private LessonMaterialAccessService accessService;

    @Mock
    private LessonMaterialChunkSearchRepository searchRepository;

    @Mock
    private EmbeddingModel embeddingModel;

    private LessonMaterialSemanticSearchServiceImpl searchService;

    @BeforeEach
    void setUp() {
        searchService =
                new LessonMaterialSemanticSearchServiceImpl(
                        accessService,
                        searchRepository,
                        embeddingModel,
                        new LessonMaterialSearchProperties(
                                MAX_DISTANCE
                        ),
                        EXPECTED_DIMENSIONS
                );
    }

    @Test
    void search_shouldEmbedQueryAndSearchAccessibleLessons() {
        String query = "What is transaction isolation?";

        List<Integer> accessibleLessonIds =
                List.of(10, 20);

        float[] queryEmbedding =
                new float[]{0.1f, 0.2f, 0.3f};

        LessonMaterialChunkSearchResult repositoryResult =
                new LessonMaterialChunkSearchResult(
                        100L,
                        30,
                        10,
                        "transactions.pdf",
                        4,
                        2,
                        "Isolation separates concurrent transactions.",
                        0.12
                );

        when(accessService.findAccessibleLessonIds(
                "student@university.com",
                "ROLE_STUDENT"
        )).thenReturn(accessibleLessonIds);

        when(embeddingModel.embed(query))
                .thenReturn(queryEmbedding);

        when(searchRepository.findNearest(
                accessibleLessonIds,
                queryEmbedding,
                MAX_DISTANCE,
                5
        )).thenReturn(List.of(repositoryResult));

        List<LessonMaterialSearchResult> actual =
                searchService.search(
                        "  " + query + "  ",
                        "student@university.com",
                        "ROLE_STUDENT",
                        5
                );

        assertEquals(
                List.of(
                        new LessonMaterialSearchResult(
                                100L,
                                30,
                                10,
                                "transactions.pdf",
                                4,
                                2,
                                "Isolation separates concurrent transactions.",
                                0.12
                        )
                ),
                actual
        );

        InOrder order = inOrder(
                accessService,
                embeddingModel,
                searchRepository
        );

        order.verify(accessService)
                .findAccessibleLessonIds(
                        "student@university.com",
                        "ROLE_STUDENT"
                );

        order.verify(embeddingModel).embed(query);

        order.verify(searchRepository).findNearest(
                accessibleLessonIds,
                queryEmbedding,
                MAX_DISTANCE,
                5
        );
    }

    @Test
    void search_shouldReturnEmptyResultWithoutCallingEmbeddingModel_whenUserHasNoLessons() {
        when(accessService.findAccessibleLessonIds(
                "student@university.com",
                "ROLE_STUDENT"
        )).thenReturn(List.of());

        List<LessonMaterialSearchResult> actual =
                searchService.search(
                        "What is transaction isolation?",
                        "student@university.com",
                        "ROLE_STUDENT",
                        5
                );

        assertEquals(List.of(), actual);

        verifyNoInteractions(
                embeddingModel,
                searchRepository
        );
    }

    @Test
    void search_shouldRejectBlankQueryBeforeCallingDependencies() {
        assertThrows(
                IllegalArgumentException.class,
                () -> searchService.search(
                        " ",
                        "student@university.com",
                        "ROLE_STUDENT",
                        5
                )
        );

        verifyNoInteractions(
                accessService,
                embeddingModel,
                searchRepository
        );
    }

    @Test
    void search_shouldRejectInvalidLimitBeforeCallingDependencies() {
        assertThrows(
                IllegalArgumentException.class,
                () -> searchService.search(
                        "What is transaction isolation?",
                        "student@university.com",
                        "ROLE_STUDENT",
                        0
                )
        );

        verifyNoInteractions(
                accessService,
                embeddingModel,
                searchRepository
        );
    }

    @Test
    void search_shouldRejectUnexpectedEmbeddingDimensions() {
        when(accessService.findAccessibleLessonIds(
                "student@university.com",
                "ROLE_STUDENT"
        )).thenReturn(List.of(10));

        when(embeddingModel.embed(
                "What is transaction isolation?"
        )).thenReturn(new float[]{0.1f, 0.2f});

        assertThrows(
                IllegalStateException.class,
                () -> searchService.search(
                        "What is transaction isolation?",
                        "student@university.com",
                        "ROLE_STUDENT",
                        5
                )
        );

        verifyNoInteractions(searchRepository);
    }
}