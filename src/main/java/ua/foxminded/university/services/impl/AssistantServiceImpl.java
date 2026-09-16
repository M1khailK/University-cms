package ua.foxminded.university.services.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.AssistantUnavailableException;
import ua.foxminded.university.services.AssistantService;
import ua.foxminded.university.services.assistant.AssistantToolContext;
import ua.foxminded.university.services.assistant.UniversityAssistantTools;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class AssistantServiceImpl implements AssistantService {

    private static final String SYSTEM_PROMPT = """
            You are the University-CMS Assistant.
            
            The current server date is %s.
            
            You do not have direct access to University-CMS private data.
            Use only application tools explicitly provided to you.
            
            When an appropriate tool is available, use it for university-specific
            information instead of inventing an answer.
            
            For schedule requests:
            - If the user provides an explicit date or date range, use that period.
            - Resolve relative dates such as "today" and "tomorrow" using the
              current server date above.
            - If the user asks for their schedule or all lessons without specifying
              a period, do not invent a date range. Ask them to provide one.
            - A schedule request must not exceed 31 days.
            - Never invent dates that the user did not request.
            
            If no appropriate tool is available, clearly state that you cannot
            access the requested University-CMS data.
            
            Never invent university-specific information.
            Never claim that you performed an action in University-CMS unless an
            explicit application tool performed that action.
            """;

    private static final Set<String> PROFILE_TOOL_ROLES = Set.of(
            "ROLE_STUDENT",
            "ROLE_TEACHER"
    );
    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
    private final UniversityAssistantTools universityAssistantTools;

    public AssistantServiceImpl(
            ObjectProvider<ChatClient.Builder> chatClientBuilderProvider,
            UniversityAssistantTools universityAssistantTools
    ) {
        this.chatClientBuilderProvider = chatClientBuilderProvider;
        this.universityAssistantTools = universityAssistantTools;
    }

    @Override
    public String answer(
            String message,
            Authentication authentication
    ) {
        ChatClient.Builder builder =
                chatClientBuilderProvider.getIfAvailable();

        if (builder == null) {
            throw new AssistantUnavailableException(
                    "AI assistant is not configured."
            );
        }

        try {
            String systemPrompt = SYSTEM_PROMPT.formatted(
                    LocalDate.now()
            );

            ChatClient.ChatClientRequestSpec requestSpec = builder
                    .defaultSystem(systemPrompt)
                    .build()
                    .prompt()
                    .user(message.strip());

            Optional<AssistantToolContext> assistantContext =
                    resolveProfileToolContext(authentication);

            if (assistantContext.isPresent()) {
                requestSpec = requestSpec
                        .tools(universityAssistantTools)
                        .toolContext(Map.of(
                                UniversityAssistantTools.ASSISTANT_CONTEXT_KEY,
                                assistantContext.get()
                        ));
            }

            String answer = requestSpec
                    .call()
                    .content();

            if (answer == null || answer.isBlank()) {
                throw new AssistantUnavailableException(
                        "AI assistant returned an empty response."
                );
            }

            return answer;

        } catch (AssistantUnavailableException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new AssistantUnavailableException(
                    "AI assistant is temporarily unavailable.",
                    exception
            );
        }
    }

    private Optional<AssistantToolContext> resolveProfileToolContext(
            Authentication authentication
    ) {
        if (authentication == null) {
            return Optional.empty();
        }

        List<String> profileRoles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(PROFILE_TOOL_ROLES::contains)
                .toList();

        if (profileRoles.size() != 1) {
            return Optional.empty();
        }

        return Optional.of(
                new AssistantToolContext(
                        authentication.getName(),
                        profileRoles.get(0)
                )
        );
    }
}
