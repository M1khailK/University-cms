package service;

import info.Group;

import java.util.List;
import java.util.Optional;

public interface GroupService {

    void save(Group group);

    Optional<Group> getById(Integer groupId);

    List<Group> getAll();

    void deleteById(Integer groupId);

}
