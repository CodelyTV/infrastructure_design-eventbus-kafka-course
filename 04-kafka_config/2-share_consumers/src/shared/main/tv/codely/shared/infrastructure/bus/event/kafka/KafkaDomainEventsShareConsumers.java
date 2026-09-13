package tv.codely.shared.infrastructure.bus.event.kafka;

import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.ShareConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ShareKafkaMessageListenerContainer;
import tv.codely.shared.domain.Service;
import tv.codely.shared.domain.bus.event.DomainEvent;
import tv.codely.shared.infrastructure.bus.event.DomainEventJsonDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Service
public final class KafkaDomainEventsShareConsumers {
    private final DomainEventShareGroups allShareGroups;
    private final ShareConsumerFactory<String, String>                     consumerFactory;
    private final DomainEventJsonDeserializer                              deserializer;
    private final ApplicationContext                                       context;
    private final List<ShareKafkaMessageListenerContainer<String, String>> containers = new ArrayList<>();

    public KafkaDomainEventsShareConsumers(
        DomainEventShareGroups allShareGroups,
        ShareConsumerFactory<String, String> consumerFactory,
        DomainEventJsonDeserializer deserializer,
        ApplicationContext context
    ) {
        this.allShareGroups  = allShareGroups;
        this.consumerFactory = consumerFactory;
        this.deserializer    = deserializer;
        this.context         = context;
    }

    public List<DomainEventShareGroup> start(BiConsumer<DomainEventShareGroup, DomainEvent> onConsumed) {
        List<DomainEventShareGroup> shareGroups = allShareGroups.all().stream().filter(this::hasSubscriberBean).toList();

        shareGroups.forEach(shareGroup -> {
            ShareKafkaMessageListenerContainer<String, String> container = containerFor(shareGroup, onConsumed);

            container.start();
            containers.add(container);
        });

        return shareGroups;
    }

    public void stop() {
        containers.forEach(ShareKafkaMessageListenerContainer::stop);
    }

    private boolean hasSubscriberBean(DomainEventShareGroup shareGroup) {
        return context.getBeanNamesForType(shareGroup.subscriberClass()).length > 0;
    }

    private ShareKafkaMessageListenerContainer<String, String> containerFor(
        DomainEventShareGroup shareGroup,
        BiConsumer<DomainEventShareGroup, DomainEvent> onConsumed
    ) {
        ContainerProperties properties = new ContainerProperties(shareGroup.topics().toArray(String[]::new));

        properties.setGroupId(shareGroup.name());
        properties.setShareAckMode(ContainerProperties.ShareAckMode.MANUAL);
        properties.setMessageListener(
            new KafkaDomainEventShareConsumer(
                shareGroup,
                context.getBean(shareGroup.subscriberClass()),
                deserializer,
                onConsumed
            )
        );

        ShareKafkaMessageListenerContainer<String, String> container = new ShareKafkaMessageListenerContainer<>(
            consumerFactory,
            properties
        );

        container.setBeanName(shareGroup.name());

        return container;
    }
}
