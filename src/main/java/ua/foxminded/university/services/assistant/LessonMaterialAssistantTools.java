package ua.foxminded.university.services.assistant;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import ua.foxminded.university.services.search
        .LessonMaterialSemanticSearchService;
import ua.foxminded.university.services.search.model
        .LessonMaterialSearchResult;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LessonMaterialAssistantTools {

    private static final int DEFAULT_RESULT_LIMIT = 5;

    private final ObjectProvider<LessonMaterialSemanticSearchService>
            searchServiceProvider;

    @Tool(
            description = """
                    Search lesson material excerpts available to the currently
                    authenticated user.

                    Use this tool when the user asks a question that may be
                    answered from uploaded lesson PDF files.

                    Pass the user's actual question as the query.

                    Base the answer only on the returned excerpts. Do not invent
                    information that is absent from the excerpts. Mention the
                    source filename and page number when useful.
                    """
    )
    public SearchResult searchLessonMaterials(
            @ToolParam(
                    description = """
                            The user's question or a concise semantic search
                            query derived from it.
                            """
            )
            String query,
            ToolContext toolContext
    ) {
        AssistantToolContext context =
                getAssistantContext(toolContext);

        LessonMaterialSemanticSearchService searchService =
                searchServiceProvider.getIfAvailable();

        if (searchService == null) {
            return SearchResult.unavailable();
        }

        List<MaterialExcerpt> excerpts =
                searchService.search(
                                query,
                                context.email(),
                                context.role(),
                                DEFAULT_RESULT_LIMIT
                        )
                        .stream()
                        .map(this::toExcerpt)
                        .toList();

        if (excerpts.isEmpty()) {
            return SearchResult.noResults();
        }

        return SearchResult.success(excerpts);
    }

    private AssistantToolContext getAssistantContext(
            ToolContext toolContext
    ) {
        Object contextValue = toolContext
                .getContext()
                .get(
                        UniversityAssistantTools
                                .ASSISTANT_CONTEXT_KEY
                );

        if (!(contextValue instanceof AssistantToolContext context)) {
            throw new IllegalStateException(
                    "Authenticated assistant context is missing."
            );
        }

        return context;
    }

    private MaterialExcerpt toExcerpt(
            LessonMaterialSearchResult source
    ) {
        return new MaterialExcerpt(
                source.lessonId(),
                source.materialId(),
                source.originalFilename(),
                source.pageNumber(),
                source.chunkIndex(),
                source.text()
        );
    }

    public record MaterialExcerpt(
            int lessonId,
            int materialId,
            String filename,
            int pageNumber,
            int chunkIndex,
            String text
    ) {
    }

    public record SearchResult(
            boolean successful,
            String message,
            List<MaterialExcerpt> excerpts
    ) {

        public static SearchResult success(
                List<MaterialExcerpt> excerpts
        ) {
            return new SearchResult(
                    true,
                    null,
                    List.copyOf(excerpts)
            );
        }

        public static SearchResult noResults() {
            return new SearchResult(
                    true,
                    "No relevant lesson material excerpts were found.",
                    List.of()
            );
        }

        public static SearchResult unavailable() {
            return new SearchResult(
                    false,
                    "Lesson material search is not configured.",
                    List.of()
            );
        }
    }
}