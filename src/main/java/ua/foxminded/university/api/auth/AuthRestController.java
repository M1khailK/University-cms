package ua.foxminded.university.api.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.auth.dto.LoginRequest;
import ua.foxminded.university.api.auth.dto.TokenResponse;
import ua.foxminded.university.services.AuthService;
import ua.foxminded.university.services.model.AccessToken;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AccessToken token = authService.login(
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(
                new TokenResponse(
                        token.value(),
                        TOKEN_TYPE,
                        token.expiresInSeconds()
                )
        );
    }
}