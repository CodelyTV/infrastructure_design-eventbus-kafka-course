package tv.codely.shared.infrastructure.bus.event.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ShareConsumer;
import org.springframework.kafka.listener.AcknowledgingShareConsumerAwareMessageListener;
import org.springframework.kafka.support.ShareAcknowledgment;
import tv.codely.shared.domain.bus.event.DomainEvent;
import tv.codely.shared.infrastructure.bus.event.DomainEventJsonDeserializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.function.BiConsumer;

public final class KafkaDomainEventShareConsumer implements AcknowledgingShareConsumerAwareMessageListener<String, String> {
    private static final Duration BACKOFF_BASE = Duration.ofSeconds(2);
    private static final Duration BACKOFF_MAX  = Duration.ofMinutes(1);

    private final DomainEventShareGroup                          shareGroup;
    private final Object                                         subscriber;
    private final Method                                         handler;
    private final DomainEventJsonDeserializer                    deserializer;
    private final BiConsumer<DomainEventShareGroup, DomainEvent> onConsumed;

    public KafkaDomainEventShareConsumer(
        DomainEventShareGroup shareGroup,
        Object subscriber,
        DomainEventJsonDeserializer deserializer,
        BiConsumer<DomainEventShareGroup, DomainEvent> onConsumed
    ) {
        this.shareGroup   = shareGroup;
        this.subscriber   = subscriber;
        this.handler      = handlerOf(subscriber);
        this.deserializer = deserializer;
        this.onConsumed   = onConsumed;
    }

    @Override
    public void onShareRecord(
        ConsumerRecord<String, String> record,
        ShareAcknowledgment acknowledgment,
        ShareConsumer<?, ?> consumer
    ) {
        DomainEvent event;

        try {
            event = deserializer.deserialize(record.value());
        } catch (Exception error) {
            acknowledgment.reject();

            return;
        }

        waitBackoffFor(record);

        try {
            handler.invoke(subscriber, event);
            acknowledgment.acknowledge();
            onConsumed.accept(shareGroup, event);
        } catch (InvocationTargetException | IllegalAccessException error) {
            acknowledgment.release();
        }
    }

    private void waitBackoffFor(ConsumerRecord<String, String> record) {
        short deliveryCount = record.deliveryCount().orElse((short) 1);

        if (deliveryCount <= 1) {
            return;
        }

        try {
            Thread.sleep(exponentialBackoffFor(deliveryCount));
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        }
    }

    private static Duration exponentialBackoffFor(short deliveryCount) {
        long multiplier = 1L << Math.min(deliveryCount - 1, 30);
        Duration backoff = BACKOFF_BASE.multipliedBy(multiplier);

        return backoff.compareTo(BACKOFF_MAX) > 0 ? BACKOFF_MAX : backoff;
    }

    private static Method handlerOf(Object subscriber) {
        return Arrays.stream(subscriber.getClass().getMethods())
                     .filter(method -> method.getName().equals("on"))
                     .filter(method -> method.getParameterCount() == 1)
                     .filter(method -> DomainEvent.class.isAssignableFrom(method.getParameterTypes()[0]))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                         String.format("The subscriber <%s> has no on(DomainEvent) method", subscriber.getClass().getName())
                     ));
    }
}
