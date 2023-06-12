package ua.foxminded.university.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.foxminded.university.customexceptions.DuplicateEmailException;
import ua.foxminded.university.customexceptions.handler.CustomExceptionHandler;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.generator.PasswordGenerator;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.EmailSenderService;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AccountCreatorController implements CustomExceptionHandler<DuplicateEmailException> {

    private static final String REDIRECT_ACCOUNT_CREATOR_PAGE = "redirect:/accountCreatorPage";
    private static final String EMAIL_TEMPLATE = "userPassword";
    private static final String SUBJECT = "User password";

    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordGenerator passwordGenerator;

    @GetMapping("/accountCreatorPage")
    public String accountCreatorPage() {
        return "accountCreator";
    }

    @PostMapping("/createStudent")
    public String createStudentAccount(@Valid User user) {
        user.setPassword(passwordGenerator.generatePassword());
        Group group = groupService.getByName(user.getGroupName());
        Student student = new Student(null, user.getFirstName(), user.getLastName(), user.getEmail(), group,
                passwordEncoder.encode(user.getPassword()));
        studentService.save(student);
        sendRegistrationEmail(user);
        return REDIRECT_ACCOUNT_CREATOR_PAGE;
    }

    @PostMapping("/createTeacher")
    public String createTeacherAccount(@Valid User user) {
        user.setPassword(passwordGenerator.generatePassword());
        Teacher teacher = new Teacher(null, user.getFirstName(), user.getLastName(), user.getEmail(),
                passwordEncoder.encode(user.getPassword()));
        teacherService.save(teacher);

        sendRegistrationEmail(user);
        return REDIRECT_ACCOUNT_CREATOR_PAGE;
    }

    @PostMapping("/createAdmin")
    public String createAdminAccount(@Valid User user) {
        user.setPassword(passwordGenerator.generatePassword());
        Teacher adminTeacher = new Teacher(null, user.getFirstName(), user.getLastName(), user.getEmail(),
                passwordEncoder.encode(user.getPassword()));
        teacherService.saveTeacherAsAdmin(adminTeacher);

        sendRegistrationEmail(user);
        return REDIRECT_ACCOUNT_CREATOR_PAGE;
    }

    private void sendRegistrationEmail(User user) {
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("name", user.getFirstName());
        templateParams.put("surname", user.getLastName());
        templateParams.put("password", user.getPassword());

        emailSenderService.sendEmail(user.getEmail(), SUBJECT, EMAIL_TEMPLATE, templateParams);
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
