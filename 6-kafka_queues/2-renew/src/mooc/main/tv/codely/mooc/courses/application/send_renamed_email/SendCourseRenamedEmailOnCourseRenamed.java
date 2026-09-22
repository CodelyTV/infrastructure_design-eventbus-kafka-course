package tv.codely.mooc.courses.application.send_renamed_email;

import org.springframework.context.event.EventListener;
import tv.codely.mooc.courses.domain.CourseId;
import tv.codely.mooc.courses.domain.CourseName;
import tv.codely.mooc.courses.domain.CourseRenamedEmailSenderGateway;
import tv.codely.shared.domain.Service;
import tv.codely.shared.domain.bus.event.DomainEventSubscriber;
import tv.codely.shared.domain.course.CourseRenamedDomainEvent;

@Service
@DomainEventSubscriber({CourseRenamedDomainEvent.class})
public final class SendCourseRenamedEmailOnCourseRenamed {
    private final CourseRenamedEmailSenderGateway gateway;

    public SendCourseRenamedEmailOnCourseRenamed(CourseRenamedEmailSenderGateway gateway) {
        this.gateway = gateway;
    }

    @EventListener
    public void on(CourseRenamedDomainEvent event) {
        CourseId   id   = new CourseId(event.aggregateId());
        CourseName name = new CourseName(event.name());

        gateway.send(id, name);
    }
}
