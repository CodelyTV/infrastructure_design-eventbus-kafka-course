package tv.codely.shared.infrastructure.bus.event.kafka;

import java.util.Map;
import java.util.TreeMap;

public record DomainEventTopic(String name) implements KafkaTopic {
    private static final Map<String, String> CONFIGS = new TreeMap<>(
        Map.of(
            "cleanup.policy", "delete",
            "retention.ms", "-1"
        )
    );

    @Override
    public Map<String, String> configs() {
        return CONFIGS;
    }
}
