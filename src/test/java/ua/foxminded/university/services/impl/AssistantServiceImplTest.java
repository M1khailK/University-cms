package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import ua.foxminded.university.customexceptions.AssistantUnavailableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssistantServiceImplTest {

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
                new AssistantServiceImpl(chatClientBuilderProvider);

        String answer = assistantService.answer("  Hello  ");

        assertEquals("Hello from assistant", answer);
        verify(requestSpec).user("Hello");
    }

    @Test
    void answer_shouldThrowAssistantUnavailable_whenChatClientIsNotConfigured() {
        when(chatClientBuilderProvider.getIfAvailable()).thenReturn(null);

        AssistantServiceImpl assistantService =
                new AssistantServiceImpl(chatClientBuilderProvider);

        AssistantUnavailableException exception = assertThrows(
                AssistantUnavailableException.class,
                () -> assistantService.answer("Hello")
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

        when(chatClient.prompt()).thenThrow(providerException);

        AssistantServiceImpl assistantService =
                new AssistantServiceImpl(chatClientBuilderProvider);

        AssistantUnavailableException exception = assertThrows(
                AssistantUnavailableException.class,
                () -> assistantService.answer("Hello")
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
                new AssistantServiceImpl(chatClientBuilderProvider);

        AssistantUnavailableException exception = assertThrows(
                AssistantUnavailableException.class,
                () -> assistantService.answer("Hello")
        );

        assertEquals(
                "AI assistant returned an empty response.",
                exception.getMessage()
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