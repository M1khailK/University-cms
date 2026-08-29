package ua.foxminded.university.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua.foxminded.university.services.AuthService;
import ua.foxminded.university.services.TokenService;
import ua.foxminded.university.services.model.AccessToken;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Override
    public AccessToken login(String email, String password) {
        Authentication authentication =
                authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated(
                                email,
                                password
                        )
                );

        return tokenService.issue(authentication);
    }
}