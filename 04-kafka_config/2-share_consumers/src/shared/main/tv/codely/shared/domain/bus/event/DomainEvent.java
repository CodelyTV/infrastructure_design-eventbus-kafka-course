package tv.codely.shared.domain.bus.event;

import tv.codely.shared.domain.Utils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public interface DomainEvent {
    String aggregateId();

    String eventId();

    String occurredOn();

    String eventName();

    HashMap<String, Serializable> toPrimitives();

    DomainEvent fromPrimitives(
        String aggregateId,
        HashMap<String, Serializable> body,
        String eventId,
        String occurredOn
    );

    static String generateEventId() {
        return UUID.randomUUID().toString();
    }

    static String now() {
        return Utils.dateToString(LocalDateTime.now());
    }
}
