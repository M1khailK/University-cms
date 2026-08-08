package ua.foxminded.university.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.GroupNotFoundException;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.repository.GroupRepository;
import ua.foxminded.university.services.GroupService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    @Override
    public void save(Group group) {
        groupRepository.save(group);
    }

    @Override
    public Group create(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public Group updateName(int groupId, String groupName) {
        Group group = getById(groupId);
        group.setName(groupName);
        return groupRepository.save(group);
    }

    @Override
    public void deleteById(int groupId) {
        Group group = getById(groupId);
        groupRepository.delete(group);
    }

    @Override
    public Group getById(int groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));
    }

    @Override
    public List<Group> getAll() {
        return groupRepository.findAll();
    }

    @Override
    public Group getByName(String groupName) {
        return groupRepository.findByName(groupName)
                .orElseThrow(() -> new GroupNotFoundException(groupName));
    }

    @Override
    public void changeNameById(int groupId, String groupName) {
        updateName(groupId, groupName);
    }


}