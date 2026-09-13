package tv.codely.shared.infrastructure.bus.event.kafka;

import tv.codely.shared.domain.Service;

import java.util.Comparator;
import java.util.List;

@Service
public final class DeadLetterTopics {
    private final DomainEventShareGroups shareGroups;

    public DeadLetterTopics(DomainEventShareGroups shareGroups) {
        this.shareGroups = shareGroups;
    }

    public List<DeadLetterTopic> all() {
        return shareGroups.all()
                          .stream()
                          .map(DomainEventShareGroup::deadLetterTopic)
                          .distinct()
                          .sorted(Comparator.comparing(DeadLetterTopic::name))
                          .toList();
    }
}
