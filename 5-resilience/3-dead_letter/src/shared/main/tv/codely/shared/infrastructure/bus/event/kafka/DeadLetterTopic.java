package tv.codely.shared.infrastructure.bus.event.kafka;

import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;

public record DeadLetterTopic(String contextName) implements KafkaTopic {
    private static final String   PREFIX    = "dlq.codely.";
    private static final Duration RETENTION = Duration.ofDays(30);

    private static final Map<String, String> CONFIGS = new TreeMap<>(
        Map.of(
            "cleanup.policy", "delete",
            "retention.ms", String.valueOf(RETENTION.toMillis()),
            "errors.deadletterqueue.group.enable", "true"
        )
    );

    @Override
    public String name() {
        return PREFIX + contextName;
    }

    @Override
    public Map<String, String> configs() {
        return CONFIGS;
    }
}
