package tv.codely.mooc.courses.application.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tv.codely.mooc.courses.CoursesModuleUnitTestCase;
import tv.codely.mooc.courses.domain.CourseCreatedDomainEventMother;
import tv.codely.mooc.courses.domain.CourseRenamedDomainEventMother;
import tv.codely.shared.domain.Logger;
import tv.codely.shared.domain.course.CourseDomainEvent;

import java.io.Serializable;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

final class LogCourseEventsOnCourseEventsShould extends CoursesModuleUnitTestCase {
    private Logger                       logger;
    private LogCourseEventsOnCourseEvents subscriber;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        logger     = mock(Logger.class);
        subscriber = new LogCourseEventsOnCourseEvents(logger);
    }

    @Test
    void log_a_course_created_event() {
        CourseDomainEvent event = CourseCreatedDomainEventMother.random();

        subscriber.on(event);

        shouldHaveLogged(event);
    }

    @Test
    void log_a_course_renamed_event() {
        CourseDomainEvent event = CourseRenamedDomainEventMother.random();

        subscriber.on(event);

        shouldHaveLogged(event);
    }

    private void shouldHaveLogged(CourseDomainEvent event) {
        HashMap<String, Serializable> context = new HashMap<>() {{
            put("event_id", event.eventId());
            put("aggregate_id", event.aggregateId());
            put("occurred_on", event.occurredOn());
            put("body", event.toPrimitives());
        }};

        verify(logger).info(eq(String.format("Course event <%s> received", event.eventName())), eq(context));
    }
}
