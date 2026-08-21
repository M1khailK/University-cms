package ua.foxminded.university.api.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.profile.dto.ProfilePasswordUpdateRequest;
import ua.foxminded.university.api.profile.dto.ProfileResponse;
import ua.foxminded.university.api.profile.mapper.ProfileMapper;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileRestController {

    private final ServiceManager serviceManager;
    private final ProfileMapper profileMapper;

    @GetMapping
    public ProfileResponse getCurrentProfile(Authentication authentication) {
        UserManagerService userManagerService = serviceManager.getUserManagerServiceByAuthentication();
        Object user = userManagerService.getByEmail(authentication.getName());

        return profileMapper.toResponse(user);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody ProfilePasswordUpdateRequest request,
                                               Authentication authentication) {
        char[] oldPassword = request.oldPassword().toCharArray();
        char[] newPassword = request.newPassword().toCharArray();

        try {
            UserManagerService userManagerService = serviceManager.getUserManagerServiceByAuthentication();
            userManagerService.changePassword(authentication.getName(), oldPassword, newPassword);

            return ResponseEntity.noContent().build();
        } finally {
            Arrays.fill(oldPassword, '\0');
            Arrays.fill(newPassword, '\0');
        }
    }
}