package ua.foxminded.university.api.profile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.profile.mapper.ProfileMapper;
import ua.foxminded.university.config.JwtConfig;
import ua.foxminded.university.config.JwtProperties;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProfileRestController.class)
@Import({SecurityConfig.class, JwtConfig.class, ProfileMapper.class})
class ProfileApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtProperties jwtProperties;

    @MockitoBean
    private ServiceManager serviceManager;

    @MockitoBean
    private UserManagerService userManagerService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void profileApiSecurity_shouldAllowReadProfile_whenUserIsStudent() throws Exception {
        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(userManagerService);
        when(userManagerService.getByEmail("student")).thenReturn(createStudent());

        mockMvc.perform(get("/api/v1/profile")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + createToken("student", "ROLE_STUDENT")
                        ))
                .andExpect(status().isOk());
    }

    @Test
    void profileApiSecurity_shouldAllowReadProfile_whenUserIsTeacher() throws Exception {
        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(userManagerService);
        when(userManagerService.getByEmail("teacher")).thenReturn(createTeacher());

        mockMvc.perform(get("/api/v1/profile")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + createToken("teacher", "ROLE_TEACHER")
                        ))
                .andExpect(status().isOk());
    }

    @Test
    void profileApiSecurity_shouldForbidReadProfile_whenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/profile")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + createToken("admin", "ROLE_ADMIN")
                        ))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.title").value("Forbidden"))
                .andExpect(jsonPath("$.detail").value(
                        "Access is denied"
                ));;
    }

    @Test
    void profileApiSecurity_shouldReturnUnauthorized_whenUserIsAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/profile")).andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.detail").value(
                        "Authentication is required"
                ));;
    }

    private String createToken(String subject, String authority) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.accessTokenTtl());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject(subject)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("authorities", List.of(authority))
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(
                        JwtEncoderParameters.from(header, claims)
                )
                .getTokenValue();
    }

    private Student createStudent() {
        Group group = new Group();
        group.setId(10);
        group.setName("AA-11");

        return new Student(
                1,
                "Alice",
                "Brown",
                "student",
                group,
                "encoded-password",
                "STUDENT"
        );
    }

    private Teacher createTeacher() {
        return new Teacher(
                2,
                "Bob",
                "Smith",
                "teacher",
                "encoded-password",
                "TEACHER"
        );
    }

    @Test
    void profileApiSecurity_shouldAllowUpdatePassword_whenUserIsStudent() throws Exception {
        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(userManagerService);

        mockMvc.perform(put("/api/v1/profile/password")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + createToken("student", "ROLE_STUDENT")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "oldPassword",
                                  "newPassword": "newPassword"
                                }
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void profileApiSecurity_shouldAllowUpdatePassword_whenUserIsTeacher() throws Exception {
        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(userManagerService);

        mockMvc.perform(put("/api/v1/profile/password")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + createToken("teacher", "ROLE_TEACHER")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "oldPassword",
                                  "newPassword": "newPassword"
                                }
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void profileApiSecurity_shouldForbidUpdatePassword_whenUserIsAdmin() throws Exception {
        mockMvc.perform(put("/api/v1/profile/password")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + createToken("admin", "ROLE_ADMIN")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "oldPassword",
                                  "newPassword": "newPassword"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void profileApiSecurity_shouldReturnUnauthorized_whenAnonymousUpdatesPassword() throws Exception {
        mockMvc.perform(put("/api/v1/profile/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "oldPassword",
                                  "newPassword": "newPassword"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void profileApiSecurity_shouldReturnUnauthorized_whenBearerTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/profile")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer invalid-token"
                        ))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.detail").value(
                        "Authentication is required"
                ));;
    }
}