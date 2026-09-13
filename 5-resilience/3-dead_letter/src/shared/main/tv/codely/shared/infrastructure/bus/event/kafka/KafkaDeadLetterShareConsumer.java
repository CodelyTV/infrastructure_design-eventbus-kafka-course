package tv.codely.shared.infrastructure.bus.event.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ShareConsumer;
import org.springframework.kafka.listener.AcknowledgingShareConsumerAwareMessageListener;
import org.springframework.kafka.support.ShareAcknowledgment;

import java.util.function.BiConsumer;

public final class KafkaDeadLetterShareConsumer implements AcknowledgingShareConsumerAwareMessageListener<String, String> {
    private final DeadLetterTopic                                 topic;
    private final DeadLetterReplayer                              replayer;
    private final BiConsumer<DeadLetterTopic, DeadLetterResolution> onResolved;

    public KafkaDeadLetterShareConsumer(
        DeadLetterTopic topic,
        DeadLetterReplayer replayer,
        BiConsumer<DeadLetterTopic, DeadLetterResolution> onResolved
    ) {
        this.topic      = topic;
        this.replayer   = replayer;
        this.onResolved = onResolved;
    }

    @Override
    public void onShareRecord(
        ConsumerRecord<String, String> record,
        ShareAcknowledgment acknowledgment,
        ShareConsumer<?, ?> consumer
    ) {
        DeadLetterResolution resolution = replayer.replay(DeadLetterMessage.fromRecord(record));

        if (resolution.action() == DeadLetterResolution.Action.RELEASED) {
            acknowledgment.release();
        } else {
            acknowledgment.acknowledge();
        }

        onResolved.accept(topic, resolution);
    }
}
