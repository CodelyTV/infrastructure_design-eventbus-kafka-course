package tv.codely.shared.domain.course;

import tv.codely.shared.domain.bus.event.DomainEvent;

public interface CourseDomainEvent extends DomainEvent {
    @Override
    default String eventName() {
        return "codely.mooc.course.*";
    }
}
