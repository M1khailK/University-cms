package ua.foxminded.university.api.group;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.group.dto.GroupResponse;
import ua.foxminded.university.api.group.mapper.GroupMapper;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.services.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ua.foxminded.university.api.group.dto.GroupCreateRequest;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupRestController {

    private final GroupService groupService;
    private final GroupMapper groupMapper;

    @GetMapping
    public List<GroupResponse> getAllGroups() {
        return groupMapper.toResponse(groupService.getAll());
    }

    @GetMapping("/{id}")
    public GroupResponse getGroupById(@PathVariable int id) {
        return groupMapper.toResponse(groupService.getById(id));
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupCreateRequest request) {
        Group createdGroup = groupService.create(groupMapper.toEntity(request));

        return ResponseEntity
                .created(URI.create("/api/v1/groups/" + createdGroup.getId()))
                .body(groupMapper.toResponse(createdGroup));
    }
}
