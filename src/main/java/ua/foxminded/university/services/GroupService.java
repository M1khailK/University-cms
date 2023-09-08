package ua.foxminded.university.services;

import ua.foxminded.university.info.Group;

public interface GroupService extends EntityService<Group> {
    Group getByName(String groupName);
    void changeNameById(String groupName,int groupId);
}
