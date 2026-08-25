package tv.codely.shared.infrastructure.bus.event.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import tv.codely.shared.domain.bus.event.DomainEvent;
import tv.codely.shared.domain.bus.event.EventBus;
import tv.codely.shared.infrastructure.bus.event.DomainEventJsonSerializer;
import tv.codely.shared.infrastructure.bus.event.failover.DomainEventFailover;
import tv.codely.shared.infrastructure.bus.event.failover.FailedDomainEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public final class KafkaEventBus implements EventBus {
    private static final int PUBLISH_TIMEOUT_IN_SECONDS = 5;
    private static final int FAILOVER_CHUNK             = 10;

    private final KafkaTemplate<String, String> template;
    private final String                        topic;
    private final DomainEventFailover           failover;

    public KafkaEventBus(KafkaTemplate<String, String> template, String topic, DomainEventFailover failover) {
        this.template = template;
        this.topic    = topic;
        this.failover = failover;
    }

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(this::publish);
    }

    public void publishFromFailover() {
        List<FailedDomainEvent> events = failover.consume(FAILOVER_CHUNK);

        events.forEach(event -> publish(event.eventId(), event.eventName(), event.body()));
    }

    private void publish(DomainEvent event) {
        publish(event.eventId(), event.eventName(), DomainEventJsonSerializer.serialize(event));
    }

    private void publish(String eventId, String eventName, String body) {
        try {
            template.send(topic, eventId, body).get(PUBLISH_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        } catch (Exception error) {
            failover.publish(eventId, eventName, body);
        }
    }
}
