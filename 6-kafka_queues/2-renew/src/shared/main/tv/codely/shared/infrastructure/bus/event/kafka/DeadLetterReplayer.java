package tv.codely.shared.infrastructure.bus.event.kafka;

import tv.codely.shared.domain.Service;
import tv.codely.shared.domain.bus.event.DomainEvent;
import tv.codely.shared.infrastructure.bus.event.DomainEventJsonDeserializer;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Service
public final class DeadLetterReplayer {
    private final DomainEventShareGroups       shareGroups;
    private final DomainEventSubscriberInvoker invoker;
    private final DomainEventJsonDeserializer  deserializer;

    public DeadLetterReplayer(
        DomainEventShareGroups shareGroups,
        DomainEventSubscriberInvoker invoker,
        DomainEventJsonDeserializer deserializer
    ) {
        this.shareGroups  = shareGroups;
        this.invoker      = invoker;
        this.deserializer = deserializer;
    }

    public DeadLetterResolution replay(DeadLetterMessage message) {
        if (message.wasRejectedByClient()) {
            return DeadLetterResolution.needsReview(message, "the body is not a valid domain event, review it by hand");
        }

        Optional<DomainEventShareGroup> shareGroup = shareGroupNamed(message.failedShareGroup());

        if (shareGroup.isEmpty()) {
            return DeadLetterResolution.needsReview(message, "no subscriber in this application for the failed share group");
        }

        DomainEvent event;

        try {
            event = deserializer.deserialize(message.body());
        } catch (Exception error) {
            return DeadLetterResolution.needsReview(message, "the copied record can not be deserialized: " + error.getMessage());
        }

        try {
            invoker.invoke(shareGroup.get().subscriberClass(), event);

            return DeadLetterResolution.replayed(message);
        } catch (InvocationTargetException error) {
            return DeadLetterResolution.released(message, error.getCause());
        } catch (IllegalAccessException error) {
            return DeadLetterResolution.released(message, error);
        }
    }

    private Optional<DomainEventShareGroup> shareGroupNamed(String name) {
        return shareGroups.all()
                          .stream()
                          .filter(shareGroup -> shareGroup.name().equals(name))
                          .filter(shareGroup -> invoker.hasSubscriber(shareGroup.subscriberClass()))
                          .findFirst();
    }
}
