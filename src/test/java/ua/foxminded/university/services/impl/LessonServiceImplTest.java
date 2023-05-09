package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.repository.LessonRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;

@SpringBootTest
public class LessonServiceImplTest {

    private static final int ID = 1;
    private static final int YEAR = 2023;
    private static final int MONTH = 3;
    private static final int DAY = 29;
    private static final LocalDate LOCAL_DATE = LocalDate.of(YEAR, MONTH, DAY);

    private static final int START_TIME_HOUR = 12;
    private static final int START_TIME_MINUTE = 40;
    private static final LocalTime START_TIME = LocalTime.of(START_TIME_HOUR, START_TIME_MINUTE);

    private static final int END_TIME_HOUR = 13;
    private static final int END_TIME_MINUTE = 40;
    private static final LocalTime END_TIME = LocalTime.of(END_TIME_HOUR, END_TIME_MINUTE);

    private static final String EXAMPLE_EMAIL = "example@gmail.com";

    private static final String GROUP_NAME = "GroupA";
    private static final String LESSON_NAME = "Lesson";

    private static final String FIRST_NAME = "Max";
    private static final String LAST_NAME = "First";

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private Clock clock;

    @Autowired
    private LessonServiceImpl lessonService;

    @Test
    void lessonService_shouldThrowAnException_whenStudentGroupIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> lessonService.getAllByStudentAndDateBetween(new Student(null,"password", FIRST_NAME, LAST_NAME, EXAMPLE_EMAIL, null), LOCAL_DATE, LOCAL_DATE));
    }

    @Test
    void lessonService_shouldReturnLessonsList_whenStudentHasGroup() {
        Group group = new Group(ID, GROUP_NAME);
        Student student = new Student(null,"password", FIRST_NAME, LAST_NAME, EXAMPLE_EMAIL, group);
        Lesson lesson = new Lesson(ID, LESSON_NAME, LOCAL_DATE, START_TIME, END_TIME, null, group, null);

        lenient().when(lessonRepository.findAllByGroupIdAndDateBetween(ID, LOCAL_DATE, LOCAL_DATE)).thenReturn(Collections.singletonList(lesson));

        Assertions.assertEquals(Collections.singletonList(lesson), lessonService.getAllByStudentAndDateBetween(student, LOCAL_DATE, LOCAL_DATE));
        Mockito.verify(lessonRepository).findAllByGroupIdAndDateBetween(ID, LOCAL_DATE, LOCAL_DATE);
    }

    @Test
    public void lessonService_shouldFindLessonsByInputDates_whenInputHasLocalDates() {
        LocalDate localDate = LocalDate.of(2020, 10, 10);
        lenient().when(lessonRepository.findAllByGroupIdAndDateBetween(1, localDate, localDate)).thenReturn(Collections.singletonList(new Lesson(ID, LESSON_NAME, localDate, START_TIME, END_TIME, null, new Group(ID, GROUP_NAME), null)));

        List<Lesson> expected = Collections.singletonList(new Lesson(ID, LESSON_NAME, localDate, START_TIME, END_TIME, null, new Group(ID, GROUP_NAME), null));
        List<Lesson> actual = lessonService.getAllByGroupAndDateBetween(new Group(ID, GROUP_NAME), localDate, localDate);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void lessonService_shouldReturnTomorrowLesson_whenInputTimeIsSixPM() {
        lenient().when(lessonRepository.findAllByGroupIdAndDateBetween(1, LocalDate.of(YEAR, MONTH, DAY + 1), LocalDate.of(YEAR, MONTH, DAY + 1))).thenReturn(Collections.singletonList(new Lesson(ID, "TOMORROW" + LESSON_NAME, LOCAL_DATE.plusDays(1), START_TIME, END_TIME, null, new Group(ID, GROUP_NAME), null)));

        List<Lesson> expected = Collections.singletonList(new Lesson(ID, "TOMORROW" + LESSON_NAME, LOCAL_DATE.plusDays(1), START_TIME, END_TIME, null, new Group(ID, GROUP_NAME), null));

        LocalTime localTime = LocalTime.of(18, 0, 0);
        LocalDate localDate = LocalDate.of(YEAR, MONTH, DAY);

        Instant fixedInstant = localTime.atDate(localDate).toInstant(ZoneOffset.UTC);
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());

        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();

        List<Lesson> actual = lessonService.getAllByGroupAndDateBetween(new Group(ID, GROUP_NAME), null, null);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void lessonService_shouldReturnTodayLesson_whenInputTimeIsNotSixPM() {
        lenient().when(lessonRepository.findAllByGroupIdAndDateBetween(1, LocalDate.of(YEAR, MONTH, DAY), LocalDate.of(YEAR, MONTH, DAY))).thenReturn(Collections.singletonList(new Lesson(ID, "TODAY" + LESSON_NAME, LOCAL_DATE, START_TIME, END_TIME, null, new Group(ID, GROUP_NAME), null)));

        List<Lesson> expected = Collections.singletonList(new Lesson(ID, "TODAY" + LESSON_NAME, LOCAL_DATE, START_TIME, END_TIME, null, new Group(ID, GROUP_NAME), null));

        LocalTime localTime = LocalTime.of(17, 59, 59);
        LocalDate localDate = LocalDate.of(YEAR, MONTH, DAY);

        Instant fixedInstant = localTime.atDate(localDate).toInstant(OffsetDateTime.now().getOffset());
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());

        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();

        List<Lesson> actual = lessonService.getAllByGroupAndDateBetween(new Group(ID, GROUP_NAME), null, null);
        Assertions.assertEquals(expected, actual);
    }

}
