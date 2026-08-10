package ua.foxminded.university.api.schedule.dto;

import java.util.List;

public record ScheduleOptionsResponse(
        List<ScheduleGroupOptionResponse> groups,
        List<ScheduleTeacherOptionResponse> teachers
) {
}
