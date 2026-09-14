package tv.codely.shared.infrastructure.bus.event.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ShareConsumer;
import org.springframework.kafka.listener.AcknowledgingShareConsumerAwareMessageListener;
import org.springframework.kafka.support.ShareAcknowledgment;
import tv.codely.shared.domain.bus.event.DomainEvent;
import tv.codely.shared.infrastructure.bus.event.DomainEventJsonDeserializer;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.function.BiConsumer;

public final class KafkaDomainEventShareConsumer implements AcknowledgingShareConsumerAwareMessageListener<String, String> {
    private static final Duration BACKOFF_STEP = Duration.ofSeconds(2);

    private final DomainEventShareGroup                          shareGroup;
    private final DomainEventSubscriberInvoker                   invoker;
    private final DomainEventJsonDeserializer                    deserializer;
    private final KafkaDomainEventLockRenewer                    lockRenewer;
    private final BiConsumer<DomainEventShareGroup, DomainEvent> onConsumed;

    public KafkaDomainEventShareConsumer(
        DomainEventShareGroup shareGroup,
        DomainEventSubscriberInvoker invoker,
        DomainEventJsonDeserializer deserializer,
        KafkaDomainEventLockRenewer lockRenewer,
        BiConsumer<DomainEventShareGroup, DomainEvent> onConsumed
    ) {
        this.shareGroup   = shareGroup;
        this.invoker      = invoker;
        this.deserializer = deserializer;
        this.lockRenewer  = lockRenewer;
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

        lockRenewer.consuming(record, consumer);

        try {
            invoker.invoke(shareGroup.subscriberClass(), event);
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
            Thread.sleep(BACKOFF_STEP.multipliedBy(deliveryCount));
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        }
    }
}
