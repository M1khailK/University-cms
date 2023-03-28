package info;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Lesson {

    private Integer id;
    private String name;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Subject subject;
    private Group group;
    private Teacher teacher;

    public Lesson(Integer id, String name, LocalDate date, LocalTime startTime, LocalTime endTime, Subject subject, Group group, Teacher teacher) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.group = group;
        this.teacher = teacher;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", subject=" + subject +
                ", group=" + group +
                ", teacher=" + teacher +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson)) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(getId(), lesson.getId()) && Objects.equals(getName(), lesson.getName()) && Objects.equals(getDate(), lesson.getDate()) && Objects.equals(getStartTime(), lesson.getStartTime()) && Objects.equals(getEndTime(), lesson.getEndTime()) && Objects.equals(getSubject(), lesson.getSubject()) && Objects.equals(getGroup(), lesson.getGroup()) && Objects.equals(getTeacher(), lesson.getTeacher());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDate(), getStartTime(), getEndTime(), getSubject(), getGroup(), getTeacher());
    }
}
