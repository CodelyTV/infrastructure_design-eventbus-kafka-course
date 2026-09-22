package tv.codely.mooc.courses.application.log;

import org.springframework.context.event.EventListener;
import tv.codely.shared.domain.Logger;
import tv.codely.shared.domain.Service;
import tv.codely.shared.domain.bus.event.DomainEventSubscriber;
import tv.codely.shared.domain.course.CourseDomainEvent;

import java.io.Serializable;
import java.util.HashMap;

@Service
@DomainEventSubscriber({CourseDomainEvent.class})
public final class LogCourseEventsOnCourseEvent {
    private final Logger logger;

    public LogCourseEventsOnCourseEvent(Logger logger) {
        this.logger = logger;
    }

    @EventListener
    public void on(CourseDomainEvent event) {
        logger.info(
            String.format("Course event <%s> received", event.eventName()),
            new HashMap<String, Serializable>() {{
                put("event_id", event.eventId());
                put("aggregate_id", event.aggregateId());
                put("occurred_on", event.occurredOn());
                put("body", event.toPrimitives());
            }}
        );
    }
}
