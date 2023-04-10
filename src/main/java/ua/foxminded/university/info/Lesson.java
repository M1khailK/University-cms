package ua.foxminded.university.info;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id", length = 50, nullable = false)
    private Integer id;

    @Column(name = "lesson_name", length = 50, nullable = false)
    private String name;

    @Column(name = "lesson_date", length = 50, nullable = false)
    private LocalDate date;

    @Column(name = "start_time", length = 50, nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", length = 50, nullable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    public Lesson() {

    }

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
