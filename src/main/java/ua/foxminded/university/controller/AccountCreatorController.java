package ua.foxminded.university.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.foxminded.university.customexceptions.DuplicateEmailException;
import ua.foxminded.university.customexceptions.handler.CustomExceptionHandler;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;

import java.time.LocalDateTime;

@Controller
public class AccountCreatorController implements CustomExceptionHandler<DuplicateEmailException> {

    private static final String REDIRECT_ACCOUNT_CREATOR_PAGE = "redirect:/createAccount";

    @Autowired
    private ServiceManager serviceManager;

    @GetMapping("/createAccount")
    public String createAccountPage() {
        return "accountCreator";
    }

    @PostMapping("/createUserAccount")
    public String createUserAccount(@Valid User user, @RequestParam String role) {
        String fullRole = "ROLE_" + role;
        UserManagerService service = serviceManager.getServiceByRole(fullRole).get();
        service.createUserAccountByRole(user, role);
        return REDIRECT_ACCOUNT_CREATOR_PAGE;
    }

    @Override
    @ExceptionHandler(DuplicateEmailException.class)
    public ModelAndView handleCustomException(DuplicateEmailException exception) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("message", exception.getMessage());

        mav.setViewName("errorPage");

        return mav;
    }
}
