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

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class AssistantServiceImpl implements AssistantService {

    private static final String SYSTEM_PROMPT = """
            You are the University-CMS Assistant.
            
            You do not have direct access to University-CMS private data.
            Use only application tools explicitly provided to you.
            
            When an appropriate tool is available, use it for university-specific
            information instead of inventing an answer.
            
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
    public String answer(String message, Authentication authentication) {
        ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();

        if (builder == null) {
            throw new AssistantUnavailableException(
                    "AI assistant is not configured."
            );
        }

        try {
            ChatClient.ChatClientRequestSpec requestSpec = builder
                    .defaultSystem(SYSTEM_PROMPT)
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

        var profileRoles = authentication.getAuthorities()
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
