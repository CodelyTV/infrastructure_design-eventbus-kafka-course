package tv.codely.shared.infrastructure.bus.event.kafka;

import tv.codely.shared.infrastructure.bus.event.DomainEventSubscriberInformation;

import java.util.List;

public record DomainEventShareGroup(String name, String contextName, Class<?> subscriberClass, List<String> topics) {
    public static String nameFor(DomainEventSubscriberInformation subscriber) {
        return String.format(
            "codely.%s.%s.%s",
            subscriber.contextName(),
            subscriber.moduleName(),
            toKebabCase(subscriber.className())
        );
    }

    public DeadLetterTopic deadLetterTopic() {
        return new DeadLetterTopic(contextName);
    }

    private static String toKebabCase(String text) {
        return text.replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase();
    }
}
