package tv.codely.shared.infrastructure.bus.event.kafka;

import java.util.Map;

public interface KafkaTopic {
    String name();

    Map<String, String> configs();

    default String terraformResourceName() {
        return name().replaceAll("[^a-zA-Z0-9]", "_");
    }
}
