package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.services.GroupService;

import java.util.List;

@Controller
public class GroupController {

    private static final String GROUP_INFO_PAGE = "groupInfoPage";
    private static final String GROUPS_ATTRIBUTE = "groups";
    @Autowired
    private GroupService groupService;


    @GetMapping("/getGroupInfoPage")
    public String getGroupInfoPage(Model model) {
        model.addAttribute(GROUPS_ATTRIBUTE, groupService.getAll());
        return GROUP_INFO_PAGE;
    }

    @GetMapping("/getGroupInfo")
    public String getGroupInfo(Model model, @RequestParam(value = "group_id") int groupId) {
        model.addAttribute(GROUPS_ATTRIBUTE, groupService.getAll());
        List<Student> students = groupService.getById(groupId).getStudents();
        model.addAttribute("students", students);
        return GROUP_INFO_PAGE;
    }

    @PostMapping("/editGroupInfo")
    public String editGroupInfo(Model model, @RequestParam(value = "group_id") int groupId, @RequestParam(value = "group_name") String groupName) {
        model.addAttribute(GROUPS_ATTRIBUTE, groupService.getAll());
        groupService.changeNameById(groupId, groupName);
        return GROUP_INFO_PAGE;
    }
}
