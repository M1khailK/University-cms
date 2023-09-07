package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.StudentService;

import java.util.List;

@Controller
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private StudentService studentService;

    @GetMapping("/getGroupInfo")
    public String getGroupInfo(Model model,@RequestParam(value = "group",required = false) Group group) {
        model.addAttribute("groups", groupService.getAll());
        List<Student> students = studentService.getStudentsByGroup(group);
        model.addAttribute("students",students);
        return "groupInfoPage";
    }
}
