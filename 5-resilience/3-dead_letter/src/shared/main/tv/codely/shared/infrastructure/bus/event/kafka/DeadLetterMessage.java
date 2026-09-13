package tv.codely.shared.infrastructure.bus.event.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public record DeadLetterMessage(
    String originalTopic,
    int originalPartition,
    long originalOffset,
    String failedShareGroup,
    Optional<Short> deliveryCount,
    String cause,
    String key,
    String body
) {
    private static final String HEADER_TOPIC          = "__dlq.errors.topic";
    private static final String HEADER_PARTITION      = "__dlq.errors.partition";
    private static final String HEADER_OFFSET         = "__dlq.errors.offset";
    private static final String HEADER_GROUP          = "__dlq.errors.group";
    private static final String HEADER_DELIVERY_COUNT = "__dlq.errors.delivery.count";
    private static final String HEADER_MESSAGE        = "__dlq.errors.message";

    private static final String CAUSE_REJECTED_BY_CLIENT      = "Offset rejected by client.";
    private static final String CAUSE_DELIVERY_COUNT_EXCEEDED = "Offset delivery count exceeded the threshold.";

    public static DeadLetterMessage fromRecord(ConsumerRecord<String, String> record) {
        return new DeadLetterMessage(
            header(record, HEADER_TOPIC).orElse("unknown"),
            header(record, HEADER_PARTITION).map(Integer::parseInt).orElse(-1),
            header(record, HEADER_OFFSET).map(Long::parseLong).orElse(-1L),
            header(record, HEADER_GROUP).orElse("unknown"),
            header(record, HEADER_DELIVERY_COUNT).map(Short::parseShort),
            header(record, HEADER_MESSAGE).orElse("unknown"),
            record.key(),
            record.value()
        );
    }

    public boolean wasRejectedByClient() {
        return CAUSE_REJECTED_BY_CLIENT.equals(cause);
    }

    public boolean exceededDeliveryCount() {
        return CAUSE_DELIVERY_COUNT_EXCEEDED.equals(cause);
    }

    private static Optional<String> header(ConsumerRecord<String, String> record, String name) {
        Header header = record.headers().lastHeader(name);

        return Optional.ofNullable(header)
                       .map(Header::value)
                       .map(value -> new String(value, StandardCharsets.UTF_8));
    }
}
