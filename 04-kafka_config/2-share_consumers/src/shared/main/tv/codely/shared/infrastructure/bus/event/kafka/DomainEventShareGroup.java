package tv.codely.shared.infrastructure.bus.event.kafka;

import tv.codely.shared.infrastructure.bus.event.DomainEventSubscriberInformation;

import java.util.List;

public record DomainEventShareGroup(String name, Class<?> subscriberClass, List<String> topics) {
    public static String nameFor(DomainEventSubscriberInformation subscriber) {
        return String.format(
            "codely.%s.%s.%s",
            subscriber.contextName(),
            subscriber.moduleName(),
            toSnakeCase(subscriber.className())
        );
    }

    private static String toSnakeCase(String text) {
        return text.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }
}
