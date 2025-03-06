package ua.foxminded.university.services;

import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;

import java.util.List;

public interface GroupService extends EntityService<Group> {
    Group getByName(String groupName);

    void changeNameById(int groupId, String groupName);
}
