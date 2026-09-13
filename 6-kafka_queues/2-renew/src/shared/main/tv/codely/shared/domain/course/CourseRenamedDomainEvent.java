package tv.codely.shared.domain.course;

import tv.codely.shared.domain.bus.event.DomainEvent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public record CourseRenamedDomainEvent(
    String aggregateId,
    String eventId,
    String occurredOn,
    String name
) implements CourseDomainEvent {

    public CourseRenamedDomainEvent() {
        this(null, null, null, null);
    }

    public CourseRenamedDomainEvent(String aggregateId, String name) {
        this(aggregateId, DomainEvent.generateEventId(), DomainEvent.now(), name);
    }

    @Override
    public String eventName() {
        return "codely.mooc.course.renamed";
    }

    @Override
    public HashMap<String, Serializable> toPrimitives() {
        return new HashMap<String, Serializable>() {{
            put("id", aggregateId);
            put("name", name);
        }};
    }

    @Override
    public CourseRenamedDomainEvent fromPrimitives(
        String aggregateId,
        HashMap<String, Serializable> body,
        String eventId,
        String occurredOn
    ) {
        return new CourseRenamedDomainEvent(
            aggregateId,
            eventId,
            occurredOn,
            (String) body.get("name")
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseRenamedDomainEvent that = (CourseRenamedDomainEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) &&
               Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, name);
    }
}
