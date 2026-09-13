package tv.codely.shared.infrastructure.bus.event.kafka;

public record DeadLetterResolution(DeadLetterMessage message, Action action, String reason) {
    public enum Action {
        REPLAYED,
        RELEASED,
        NEEDS_REVIEW
    }

    public static DeadLetterResolution replayed(DeadLetterMessage message) {
        return new DeadLetterResolution(message, Action.REPLAYED, "the subscriber consumed it");
    }

    public static DeadLetterResolution released(DeadLetterMessage message, Throwable error) {
        return new DeadLetterResolution(message, Action.RELEASED, "the subscriber failed again: " + error.getMessage());
    }

    public static DeadLetterResolution needsReview(DeadLetterMessage message, String reason) {
        return new DeadLetterResolution(message, Action.NEEDS_REVIEW, reason);
    }
}
