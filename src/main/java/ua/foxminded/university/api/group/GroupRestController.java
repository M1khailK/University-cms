package ua.foxminded.university.api.group;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.group.dto.GroupCreateRequest;
import ua.foxminded.university.api.group.dto.GroupResponse;
import ua.foxminded.university.api.group.dto.GroupStudentResponse;
import ua.foxminded.university.api.group.dto.GroupUpdateRequest;
import ua.foxminded.university.api.group.mapper.GroupMapper;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.services.GroupService;

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
        return groupMapper.toResponses(groupService.getAll());
    }

    @GetMapping("/{id}/students")
    public List<GroupStudentResponse> getGroupStudents(@PathVariable int id) {
        return groupMapper.toStudentResponses(groupService.getById(id).getStudents());
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

    @PutMapping("/{id}")
    public GroupResponse updateGroup(@PathVariable int id, @Valid @RequestBody GroupUpdateRequest request) {
        return groupMapper.toResponse(groupService.updateName(id, request.name()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable int id) {
        groupService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
