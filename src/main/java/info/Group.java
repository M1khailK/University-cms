package info;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Group {

    private Integer id;
    private String name;
    private final Set<Student> students = new HashSet<>();

    public Group(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", students=" + students +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        Group group = (Group) o;
        return Objects.equals(getId(), group.getId()) && Objects.equals(getName(), group.getName()) && Objects.equals(students, group.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), students);
    }
}
