package ua.foxminded.university.customexceptions;

public class GroupNotFoundException extends IllegalArgumentException {
    public GroupNotFoundException(int groupId) {
        super("Group was not found by id: " + groupId);
    }

    public GroupNotFoundException(String groupName) {
        super("Group was not found by name: " + groupName);
    }
}
