package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.repository.GroupRepository;
import ua.foxminded.university.services.GroupService;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public void save(Group group) {
        groupRepository.save(group);
    }

    @Override
    public Group getById(int groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group was not found by id"));
    }

    @Override
    public List<Group> getAll() {
        return groupRepository.findAll();
    }

    @Override
    public Group getByName(String groupName) {
        return groupRepository.findByName(groupName).orElseThrow(() -> new IllegalArgumentException("Group was not found by name"));
    }

    @Override
    public void changeNameById(int groupId, String groupName) {
        Group group = getById(groupId);
        group.setName(groupName);
        save(group);
    }

}
