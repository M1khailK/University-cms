package ua.foxminded.university.api.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.schedule.dto.ScheduleLessonResponse;
import ua.foxminded.university.api.schedule.dto.ScheduleOptionsResponse;
import ua.foxminded.university.api.schedule.mapper.ScheduleMapper;
import ua.foxminded.university.customexceptions.InvalidDateRangeException;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleRestController {

    private final LessonService lessonService;
    private final GroupService groupService;
    private final TeacherService teacherService;
    private final ScheduleMapper scheduleMapper;

    @GetMapping("/groups/{groupId}")
    public List<ScheduleLessonResponse> getGroupSchedule(
            @PathVariable int groupId,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {
        validateDateRange(from, to);

        Group group = groupService.getById(groupId);

        return scheduleMapper.toResponses(
                lessonService.getAllByGroupAndDateBetween(group, from, to)
        );
    }

    @GetMapping("/teachers/{teacherId}")
    public List<ScheduleLessonResponse> getTeacherSchedule(
            @PathVariable int teacherId,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {
        validateDateRange(from, to);

        Teacher teacher = teacherService.getById(teacherId);

        return scheduleMapper.toResponses(
                lessonService.getAllByTeacherAndDateBetween(teacher, from, to)
        );
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from == null && to != null) {
            throw new InvalidDateRangeException("From date cannot be null when To date is provided.");
        }
    }

    @GetMapping("/options")
    public ScheduleOptionsResponse getScheduleOptions() {
        return new ScheduleOptionsResponse(
                scheduleMapper.toGroupOptionResponses(groupService.getAll()),
                scheduleMapper.toTeacherOptionResponses(teacherService.getAll())
        );
    }
}