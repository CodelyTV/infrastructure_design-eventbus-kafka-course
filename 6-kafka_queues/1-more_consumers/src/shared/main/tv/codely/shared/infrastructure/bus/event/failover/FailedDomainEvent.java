package tv.codely.shared.infrastructure.bus.event.failover;

public record FailedDomainEvent(String eventId, String eventName, String body) {
}
