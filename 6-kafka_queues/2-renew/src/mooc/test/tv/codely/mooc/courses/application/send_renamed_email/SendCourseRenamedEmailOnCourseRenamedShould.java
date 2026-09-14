package tv.codely.mooc.courses.application.send_renamed_email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tv.codely.mooc.courses.CoursesModuleUnitTestCase;
import tv.codely.mooc.courses.domain.CourseId;
import tv.codely.mooc.courses.domain.CourseName;
import tv.codely.mooc.courses.domain.CourseRenamedDomainEventMother;
import tv.codely.mooc.courses.domain.CourseRenamedEmailSenderGateway;
import tv.codely.shared.domain.course.CourseRenamedDomainEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

final class SendCourseRenamedEmailOnCourseRenamedShould extends CoursesModuleUnitTestCase {
    private CourseRenamedEmailSenderGateway              sender;
    private SendCourseRenamedEmailOnCourseRenamed subscriber;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        sender     = mock(CourseRenamedEmailSenderGateway.class);
        subscriber = new SendCourseRenamedEmailOnCourseRenamed(sender);
    }

    @Test
    void send_the_renamed_email() {
        CourseRenamedDomainEvent event = CourseRenamedDomainEventMother.random();

        subscriber.on(event);

        verify(sender).send(new CourseId(event.aggregateId()), new CourseName(event.name()));
    }
}
