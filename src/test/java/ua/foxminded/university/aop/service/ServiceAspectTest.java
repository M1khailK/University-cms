package ua.foxminded.university.aop.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import ua.foxminded.university.config.aspect.ServiceAspectTestConfig;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.repository.GroupRepository;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.impl.GroupServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest()
@ContextConfiguration(classes = ServiceAspectTestConfig.class)
public class ServiceAspectTest {

    private static final String GROUP_NAME = "Group";
    private static final int GROUP_ID = 1;

    @MockBean
    private GroupRepository groupRepository;

    @Autowired
    private GroupServiceImpl groupService;

    @Test
    void serviceAspect_shouldDoLogging_whenGroupServiceAddNewGroup() {
        Logger logger = (Logger) LoggerFactory.getLogger(ServiceAspect.class);

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        logger.addAppender(listAppender);
        listAppender.start();

        groupService.save(new Group(null, GROUP_NAME, Collections.emptyList()));
        List<ILoggingEvent> logList = listAppender.list;

        String firstExpected = "Calling: void ua.foxminded.university.services.impl.GroupServiceImpl.save(Group)";
        String secondExpected = "void ua.foxminded.university.services.impl.GroupServiceImpl.save(Group) response: null";
        Assertions.assertEquals(firstExpected, logList.get(0).getFormattedMessage());
        Assertions.assertEquals(secondExpected, logList.get(1).getFormattedMessage());
        Assertions.assertEquals(Level.TRACE, logList.get(0).getLevel());
        Assertions.assertEquals(Level.TRACE, logList.get(1).getLevel());
    }

    @Test
    void serviceAspect_shouldDoLogging_whenGroupServiceGetGroupById() {
        when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(new Group(1, GROUP_NAME,Collections.emptyList())));

        Logger logger = (Logger) LoggerFactory.getLogger(ServiceAspect.class);

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        logger.addAppender(listAppender);
        listAppender.start();

        groupService.getById(GROUP_ID);
        List<ILoggingEvent> logList = listAppender.list;

        String firstExpected = "Calling: Group ua.foxminded.university.services.impl.GroupServiceImpl.getById(int)";
        String secondExpected = "Group ua.foxminded.university.services.impl.GroupServiceImpl.getById(int) response: Group{id=1, name='Group'}";
        Assertions.assertEquals(firstExpected, logList.get(0).getFormattedMessage());
        Assertions.assertEquals(secondExpected, logList.get(1).getFormattedMessage());
        Assertions.assertEquals(Level.TRACE, logList.get(0).getLevel());
        Assertions.assertEquals(Level.TRACE, logList.get(1).getLevel());
    }


}
