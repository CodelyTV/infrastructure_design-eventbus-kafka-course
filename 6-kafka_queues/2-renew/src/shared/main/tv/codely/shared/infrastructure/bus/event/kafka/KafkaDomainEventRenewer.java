package tv.codely.shared.infrastructure.bus.event.kafka;

import org.apache.kafka.clients.consumer.AcknowledgeType;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ShareConsumer;
import tv.codely.shared.domain.Service;

@Service
public final class KafkaDomainEventRenewer {
    private final ThreadLocal<Runnable> currentRenewal = new ThreadLocal<>();

    public void registerRenewal(ConsumerRecord<String, String> record, ShareConsumer<?, ?> consumer) {
        currentRenewal.set(() -> {
            consumer.acknowledge(record.topic(), record.partition(), record.offset(), AcknowledgeType.RENEW);
            consumer.commitSync();
        });
    }

    public void renew() {
        currentRenewal.get().run();
    }
}
