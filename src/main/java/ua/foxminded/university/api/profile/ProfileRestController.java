package ua.foxminded.university.api.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.profile.dto.ProfileResponse;
import ua.foxminded.university.api.profile.mapper.ProfileMapper;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;

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
}