package ua.foxminded.university.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.foxminded.university.customexceptions.DuplicateEmailException;
import ua.foxminded.university.customexceptions.handler.CustomExceptionHandler;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.AccountCreatorService;

import java.time.LocalDateTime;

@Controller
public class AccountCreatorController implements CustomExceptionHandler<DuplicateEmailException> {

    private static final String REDIRECT_ACCOUNT_CREATOR_PAGE = "redirect:/createAccount";
    private static final String STUDENT = "STUDENT";
    private static final String TEACHER = "TEACHER";
    private static final String ADMIN = "ADMIN";

    @Autowired
    private AccountCreatorService accountCreatorService;
    @Autowired
    private ServiceManager serviceManager;

    @GetMapping("/createAccount")
    public String createAccountPage() {
        return "accountCreator";
    }

    @PostMapping("/createStudent")
    public String createStudentAccount(@Valid User user) {
        accountCreatorService.createUserAccount(user, STUDENT);
        return REDIRECT_ACCOUNT_CREATOR_PAGE;
    }

    @PostMapping("/createTeacher")
    public String createTeacherAccount(@Valid User user) {
        accountCreatorService.createUserAccount(user, TEACHER);
        return REDIRECT_ACCOUNT_CREATOR_PAGE;
    }

    @PostMapping("/createAdmin")
    public String createAdminAccount(@Valid User user) {
        accountCreatorService.createUserAccount(user, ADMIN);
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
