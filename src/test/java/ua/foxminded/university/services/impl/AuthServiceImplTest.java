package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import ua.foxminded.university.services.TokenService;
import ua.foxminded.university.services.model.AccessToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_shouldReturnAccessToken_whenCredentialsAreValid() {
        Authentication authenticated = mock(Authentication.class);
        AccessToken expectedToken = new AccessToken("jwt-token", 900);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authenticated);

        when(tokenService.issue(authenticated))
                .thenReturn(expectedToken);

        AccessToken actualToken = authService.login(
                "student@example.com",
                "password"
        );

        assertEquals(expectedToken, actualToken);

        verify(authenticationManager).authenticate(
                argThat(authentication ->
                        "student@example.com".equals(authentication.getName())
                                && "password".equals(authentication.getCredentials()))
        );

        verify(tokenService).issue(authenticated);
    }

    @Test
    void login_shouldPropagateAuthenticationException_whenCredentialsAreInvalid() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(
                        "student@example.com",
                        "wrong-password"
                )
        );

        verifyNoInteractions(tokenService);
    }
}