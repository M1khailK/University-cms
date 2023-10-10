package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.services.SubjectService;

@Controller
public class SubjectController {

    private static final String REDIRECT_CREATE_SUBJECT = "redirect:/createUniversitySubject";
    private static final String SUBJECTS_ATTRIBUTE = "subjects";

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/createUniversitySubject")
    public String createSubjectPage(Model model) {
        model.addAttribute(SUBJECTS_ATTRIBUTE, subjectService.getAll());
        return "subjectCreator";
    }

    @PostMapping("/createSubject")
    public String createSubject(Model model,@RequestParam String name) {
        model.addAttribute(SUBJECTS_ATTRIBUTE, subjectService.getAll());
        Subject subject = new Subject(null, name);
        subjectService.save(subject);
        return REDIRECT_CREATE_SUBJECT;
    }

    @PostMapping("/editSubject")
    public String editSubject(Model model, @RequestParam("subject_id") int subjectId, @RequestParam("subject_name") String subjectName) {
        model.addAttribute(SUBJECTS_ATTRIBUTE, subjectService.getAll());
        subjectService.changeNameById(subjectId, subjectName);
        return REDIRECT_CREATE_SUBJECT;
    }
}
