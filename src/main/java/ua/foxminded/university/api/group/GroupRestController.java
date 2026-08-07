package ua.foxminded.university.api.group;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.group.dto.GroupResponse;
import ua.foxminded.university.api.group.mapper.GroupMapper;
import ua.foxminded.university.services.GroupService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupRestController {

    private final GroupService groupService;
    private final GroupMapper groupMapper;

    @GetMapping
    public List<GroupResponse> getAllGroups() {
        return groupMapper.toResponses(groupService.getAll());
    }

    @GetMapping("/{id}")
    public GroupResponse getGroupById(@PathVariable int id) {
        return groupMapper.toResponses(groupService.getById(id));
    }
}
