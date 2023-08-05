package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.services.SubjectService;

@Controller
public class SubjectCreatorController {

    private static final String REDIRECT_CREATE_SUBJECT = "redirect:/createUniversitySubject";

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/createUniversitySubject")
    public String createSubjectPage() {
        return "subjectCreator";
    }

    @PostMapping("/createSubject")
    public String createSubject(@RequestParam String name) {
        Subject subject = new Subject(null, name);
        subjectService.save(subject);
        return REDIRECT_CREATE_SUBJECT;
    }
}
