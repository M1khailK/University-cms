package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import ua.foxminded.university.customexceptions.AssistantUnavailableException;
import ua.foxminded.university.services.assistant.AssistantToolContext;
import ua.foxminded.university.services.assistant.UniversityAssistantTools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssistantServiceImplTest {

    @Mock
    private UniversityAssistantTools universityAssistantTools;

    @Mock
    private ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    @Test
    void answer_shouldReturnResponse_whenChatClientIsAvailable() {
        configureChatClient();

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Hello")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("Hello from assistant");

        AssistantServiceImpl assistantService =
                new AssistantServiceImpl(
                        chatClientBuilderProvider,
                        universityAssistantTools
                );

        String answer = assistantService.answer(
                "Hello",
                adminAuthentication()
        );

        assertEquals("Hello from assistant", answer);

        verify(requestSpec).user("Hello");
        verify(requestSpec, never())
                .tools(universityAssistantTools);
        verify(requestSpec, never())
                .toolContext(anyMap());
    }

    @Test
    void answer_shouldExposeProfileToolWithTrustedContext_whenUserIsStudent() {
        configureChatClient();

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("What is my profile?"))
                .thenReturn(requestSpec);
        when(requestSpec.tools(universityAssistantTools))
                .thenReturn(requestSpec);
        when(requestSpec.toolContext(anyMap()))
                .thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content())
                .thenReturn("Your name is Alice.");

        AssistantServiceImpl assistantService =
                new AssistantServiceImpl(
                        chatClientBuilderProvider,
                        universityAssistantTools
                );

        Authentication authentication =
                new TestingAuthenticationToken(
                        "student@university.com",
                        "password",
                        "ROLE_STUDENT"
                );

        String answer = assistantService.answer(
                "What is my profile?",
                authentication
        );

        assertEquals("Your name is Alice.", answer);

        verify(requestSpec)
                .tools(universityAssistantTools);

        verify(requestSpec)
                .toolContext(
                        argThat(context ->
                                new AssistantToolContext(
                                        "student@university.com",
                                        "ROLE_STUDENT"
                                ).equals(
                                        context.get(
                                                UniversityAssistantTools
                                                        .ASSISTANT_CONTEXT_KEY
                                        )
                                )
                        )
                );
    }

    @Test
    void answer_shouldThrowAssistantUnavailable_whenChatClientIsNotConfigured() {
        when(chatClientBuilderProvider.getIfAvailable())
                .thenReturn(null);

        AssistantServiceImpl assistantService =
                new AssistantServiceImpl(
                        chatClientBuilderProvider,
                        universityAssistantTools
                );

        AssistantUnavailableException exception = assertThrows(
                AssistantUnavailableException.class,
                () -> assistantService.answer(
                        "Hello",
                        adminAuthentication()
                )
        );

        assertEquals(
                "AI assistant is not configured.",
                exception.getMessage()
        );
    }

    @Test
    void answer_shouldThrowAssistantUnavailable_whenProviderCallFails() {
        configureChatClient();

        RuntimeException providerException =
                new RuntimeException("Provider unavailable");

        when(chatClient.prompt())
                .thenThrow(providerException);

        AssistantServiceImpl assistantService =
                new AssistantServiceImpl(
                        chatClientBuilderProvider,
                        universityAssistantTools
                );

        AssistantUnavailableException exception = assertThrows(
                AssistantUnavailableException.class,
                () -> assistantService.answer(
                        "Hello",
                        adminAuthentication()
                )
        );

        assertEquals(
                "AI assistant is temporarily unavailable.",
                exception.getMessage()
        );
        assertEquals(providerException, exception.getCause());
    }

    @Test
    void answer_shouldThrowAssistantUnavailable_whenProviderReturnsBlankResponse() {
        configureChatClient();

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Hello")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("   ");

        AssistantServiceImpl assistantService =
                new AssistantServiceImpl(
                        chatClientBuilderProvider,
                        universityAssistantTools
                );

        AssistantUnavailableException exception = assertThrows(
                AssistantUnavailableException.class,
                () -> assistantService.answer(
                        "Hello",
                        adminAuthentication()
                )
        );

        assertEquals(
                "AI assistant returned an empty response.",
                exception.getMessage()
        );
    }

    private Authentication adminAuthentication() {
        return new TestingAuthenticationToken(
                "admin@university.com",
                "password",
                "ROLE_ADMIN"
        );
    }

    private void configureChatClient() {
        when(chatClientBuilderProvider.getIfAvailable())
                .thenReturn(chatClientBuilder);

        when(chatClientBuilder.defaultSystem(anyString()))
                .thenReturn(chatClientBuilder);

        when(chatClientBuilder.build())
                .thenReturn(chatClient);
    }
}