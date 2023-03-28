package service.impl;

import info.Group;
import repository.GroupRepository;
import service.Service;

import java.util.List;
import java.util.Optional;

public class GroupServiceImpl implements Service<Group> {

    private GroupRepository groupRepository;

    @Override
    public void save(Group group) {
        groupRepository.save(group);
    }

    @Override
    public Optional<Group> getById(Integer groupId) {
        return groupRepository.findById(groupId);
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
