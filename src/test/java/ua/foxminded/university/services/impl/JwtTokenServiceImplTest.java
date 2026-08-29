package ua.foxminded.university.services.impl;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import ua.foxminded.university.config.JwtProperties;
import ua.foxminded.university.services.model.AccessToken;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtTokenServiceImplTest {

    private static final Instant NOW =
            Instant.parse("2026-08-27T16:00:00Z");

    private static final Clock CLOCK =
            Clock.fixed(NOW, ZoneOffset.UTC);

    private static final Duration ACCESS_TOKEN_TTL =
            Duration.ofMinutes(15);

    @Test
    void issue_shouldCreateSignedJwtWithExpectedClaims() {
        SecretKey secretKey = new SecretKeySpec(
                "0123456789abcdef0123456789abcdef"
                        .getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        JWKSource<SecurityContext> jwkSource =
                new ImmutableSecret<>(secretKey);

        JwtEncoder jwtEncoder =
                new NimbusJwtEncoder(jwkSource);

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        JwtTimestampValidator timestampValidator =
                new JwtTimestampValidator();

        timestampValidator.setClock(CLOCK);

        jwtDecoder.setJwtValidator(timestampValidator);

        JwtProperties jwtProperties = new JwtProperties(
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
                "university-cms-test",
                ACCESS_TOKEN_TTL
        );

        JwtTokenServiceImpl tokenService =
                new JwtTokenServiceImpl(
                        jwtEncoder,
                        jwtProperties,
                        CLOCK
                );

        Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        "student@example.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_STUDENT")
                        )
                );

        AccessToken accessToken =
                tokenService.issue(authentication);

        Jwt jwt = jwtDecoder.decode(accessToken.value());

        assertEquals(
                "student@example.com",
                jwt.getSubject()
        );

        assertEquals(
                "university-cms-test",
                jwt.getClaimAsString("iss")
        );

        assertEquals(
                NOW,
                jwt.getIssuedAt()
        );

        assertEquals(
                NOW.plus(ACCESS_TOKEN_TTL),
                jwt.getExpiresAt()
        );

        assertEquals(
                List.of("ROLE_STUDENT"),
                jwt.getClaimAsStringList("authorities")
        );

        assertEquals(
                ACCESS_TOKEN_TTL.toSeconds(),
                accessToken.expiresInSeconds()
        );
    }
}