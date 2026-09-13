package tv.codely.shared.infrastructure.bus.event.kafka;

import tv.codely.shared.domain.Service;
import tv.codely.shared.infrastructure.bus.event.DomainEventsInformation;

import java.util.List;

@Service
public final class DomainEventTopics {
    private final DomainEventsInformation domainEventsInformation;

    public DomainEventTopics(DomainEventsInformation domainEventsInformation) {
        this.domainEventsInformation = domainEventsInformation;
    }

    public List<DomainEventTopic> all() {
        return domainEventsInformation.eventNames().stream().map(DomainEventTopic::new).toList();
    }
}
