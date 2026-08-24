package tv.codely.shared.infrastructure.bus.event.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import tv.codely.shared.domain.bus.event.DomainEvent;
import tv.codely.shared.domain.bus.event.EventBus;
import tv.codely.shared.infrastructure.bus.event.DomainEventJsonSerializer;

import java.util.List;

public final class KafkaEventBus implements EventBus {
    private final KafkaTemplate<String, String> template;
    private final String                        topic;

    public KafkaEventBus(KafkaTemplate<String, String> template, String topic) {
        this.template = template;
        this.topic    = topic;
    }

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(this::publish);
    }

    private void publish(DomainEvent event) {
        template.send(topic, event.aggregateId(), DomainEventJsonSerializer.serialize(event));
    }
}
