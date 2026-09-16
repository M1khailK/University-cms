package ua.foxminded.university.services.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.AssistantUnavailableException;
import ua.foxminded.university.services.AssistantService;

@Service
public class AssistantServiceImpl implements AssistantService {

    private static final String SYSTEM_PROMPT = """
            You are the University-CMS Assistant.
            
            At this stage you do not have access to university records,
            schedules, grades, policies, or private user data.
            
            Do not invent university-specific information.
            If a user asks for information that requires access to University-CMS
            data, clearly state that you do not have access to that data yet.
            
            Never claim that you performed an action in University-CMS
            unless the application explicitly provided a tool for that action.
            """;

    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;

    public AssistantServiceImpl(
            ObjectProvider<ChatClient.Builder> chatClientBuilderProvider
    ) {
        this.chatClientBuilderProvider = chatClientBuilderProvider;
    }

    @Override
    public String answer(String message) {
        ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();

        if (builder == null) {
            throw new AssistantUnavailableException(
                    "AI assistant is not configured."
            );
        }

        try {
            String answer = builder
                    .defaultSystem(SYSTEM_PROMPT)
                    .build()
                    .prompt()
                    .user(message.strip())
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
}