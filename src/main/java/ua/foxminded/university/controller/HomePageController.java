package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ua.foxminded.university.services.impl.GradeService;

@Controller
public class HomePageController {

    @Autowired
    private GradeService gradeService;

    @GetMapping("/")
    public String homepage() {
        return "home";
    }

}
