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
import ua.foxminded.university.services.LessonService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;

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
    private LessonService lessonService;


    @Test
    void lessonService_shouldThrowAnException_whenStudentGroupIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> lessonService.getAllByStudentAndDateBetween(new Student(null, FIRST_NAME, LAST_NAME, EXAMPLE_EMAIL, null), LOCAL_DATE, LOCAL_DATE));
    }

    @Test
    void lessonService_shouldReturnLessonsList_whenStudentHasGroup() {
        Group group = new Group(ID, GROUP_NAME);
        Student student = new Student(null, FIRST_NAME, LAST_NAME, EXAMPLE_EMAIL, group);
        Lesson lesson = new Lesson(ID, LESSON_NAME, LOCAL_DATE, START_TIME, END_TIME, null, group, null);

        lenient().when(lessonRepository.findAllByGroupIdAndDateBetween(ID, LOCAL_DATE, LOCAL_DATE)).thenReturn(Collections.singletonList(lesson));

        Assertions.assertEquals(Collections.singletonList(lesson), lessonService.getAllByStudentAndDateBetween(student, LOCAL_DATE, LOCAL_DATE));
        Mockito.verify(lessonRepository).findAllByGroupIdAndDateBetween(ID, LOCAL_DATE, LOCAL_DATE);
    }

    @Test
    public void setTodayOrTomorrowDateSetter_shouldDoNothing_whenInputLocalDateIsNotNull() {
        LocalDate expected = LocalDate.of(2020, 10, 10);
        LocalDate actual = lessonService.setTodayOrTomorrowDate(expected);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void setTodayOrTomorrowDateSetter_shouldReturnTomorrowDate_whenInputTimeIsSixPM() {
        LocalTime localTime = LocalTime.of(18, 0, 0);
        Instant fixedInstant = localTime.atDate(LocalDate.now()).toInstant(ZoneOffset.UTC);
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();
        LocalDate actual = lessonService.setTodayOrTomorrowDate(null);
        Assertions.assertEquals(LocalDate.now().plusDays(1), actual);
    }

    @Test
    public void setTodayOrTomorrowDateSetter_shouldReturnTodayDate_whenInputTimeIsNotSixPM() {
        LocalTime localTime = LocalTime.of(17, 59, 59);
        Instant fixedInstant = localTime.atDate(LocalDate.now()).toInstant(OffsetDateTime.now().getOffset());
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();
        LocalDate actual = lessonService.setTodayOrTomorrowDate(null);
        Assertions.assertEquals(LocalDate.now(fixedClock.getZone()), actual);
    }

}
