package tv.codely.shared.infrastructure.bus.event.kafka;

import tv.codely.shared.domain.Service;
import tv.codely.shared.domain.bus.event.DomainEvent;
import tv.codely.shared.infrastructure.bus.event.DomainEventSubscriberInformation;
import tv.codely.shared.infrastructure.bus.event.DomainEventSubscribersInformation;
import tv.codely.shared.infrastructure.bus.event.DomainEventsInformation;

import java.util.Comparator;
import java.util.List;

@Service
public final class DomainEventShareGroups {
    private final DomainEventSubscribersInformation subscribersInformation;
    private final DomainEventsInformation           domainEventsInformation;

    public DomainEventShareGroups(
        DomainEventSubscribersInformation subscribersInformation,
        DomainEventsInformation domainEventsInformation
    ) {
        this.subscribersInformation  = subscribersInformation;
        this.domainEventsInformation = domainEventsInformation;
    }

    public List<DomainEventShareGroup> all() {
        return subscribersInformation.all()
                                     .stream()
                                     .map(this::shareGroupFor)
                                     .sorted(Comparator.comparing(DomainEventShareGroup::name))
                                     .toList();
    }

    private DomainEventShareGroup shareGroupFor(DomainEventSubscriberInformation subscriber) {
        return new DomainEventShareGroup(
            DomainEventShareGroup.nameFor(subscriber),
            subscriber.subscriberClass(),
            topicsFor(subscriber)
        );
    }

    private List<String> topicsFor(DomainEventSubscriberInformation subscriber) {
        return subscriber.subscribedEvents()
                         .stream()
                         .flatMap(event -> topicsFor(event).stream())
                         .distinct()
                         .sorted()
                         .toList();
    }

    private List<String> topicsFor(Class<? extends DomainEvent> event) {
        if (event.equals(DomainEvent.class)) {
            return domainEventsInformation.eventNames();
        }

        return List.of(domainEventsInformation.forClass(event));
    }
}
