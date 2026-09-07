package tv.codely.shared.infrastructure.bus.event;

import tv.codely.shared.domain.Utils;
import tv.codely.shared.domain.bus.event.DomainEvent;

import java.io.Serializable;
import java.util.HashMap;

public final class DomainEventJsonSerializer {
    public static String serialize(DomainEvent domainEvent) {
        HashMap<String, Serializable> attributes = domainEvent.toPrimitives();

        return Utils.jsonEncode(new HashMap<>() {{
			put("id", domainEvent.eventId());
			put("name", domainEvent.eventName());
			put("aggregate_id", domainEvent.aggregateId());
			put("attributes", attributes);
			put("metadata", new HashMap<String, Serializable>());
			put("occurred_at", domainEvent.occurredOn());
		}});
    }
}
