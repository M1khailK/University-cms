package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.info.Group;
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
    public Group getById(Integer groupId) {
        return groupRepository.findById(groupId).get();
    }

    @Override
    public List<Group> getAll() {
        return groupRepository.findAll();
    }

    @Override
    public void deleteById(Integer groupId) {
        groupRepository.deleteById(groupId);
    }
}
