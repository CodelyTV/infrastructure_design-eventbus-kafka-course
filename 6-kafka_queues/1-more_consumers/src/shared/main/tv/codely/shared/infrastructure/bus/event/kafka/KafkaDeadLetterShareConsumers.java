package tv.codely.shared.infrastructure.bus.event.kafka;

import org.springframework.kafka.core.ShareConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ShareKafkaMessageListenerContainer;
import tv.codely.shared.domain.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Service
public final class KafkaDeadLetterShareConsumers {
    private static final String SHARE_GROUP_SUFFIX = ".dead-letter";

    private final DomainEventShareGroups                                   allShareGroups;
    private final KafkaShareGroupsDeadLetterConfigurer                     configurer;
    private final DeadLetterReplayer                                       replayer;
    private final ShareConsumerFactory<String, String>                     consumerFactory;
    private final DomainEventSubscriberInvoker                             invoker;
    private final List<ShareKafkaMessageListenerContainer<String, String>> containers = new ArrayList<>();

    public KafkaDeadLetterShareConsumers(
        DomainEventShareGroups allShareGroups,
        KafkaShareGroupsDeadLetterConfigurer configurer,
        DeadLetterReplayer replayer,
        ShareConsumerFactory<String, String> consumerFactory,
        DomainEventSubscriberInvoker invoker
    ) {
        this.allShareGroups  = allShareGroups;
        this.configurer      = configurer;
        this.replayer        = replayer;
        this.consumerFactory = consumerFactory;
        this.invoker         = invoker;
    }

    public List<DeadLetterTopic> start(BiConsumer<DeadLetterTopic, DeadLetterResolution> onResolved) {
        List<DeadLetterTopic> topics = allShareGroups.all()
                                                     .stream()
                                                     .filter(this::hasSubscriberBean)
                                                     .map(DomainEventShareGroup::deadLetterTopic)
                                                     .distinct()
                                                     .toList();

        configurer.configureDeadLetterConsumers(topics.stream().map(KafkaDeadLetterShareConsumers::shareGroupNameFor).toList());

        topics.forEach(topic -> {
            ShareKafkaMessageListenerContainer<String, String> container = containerFor(topic, onResolved);

            container.start();
            containers.add(container);
        });

        return topics;
    }

    public void stop() {
        containers.forEach(ShareKafkaMessageListenerContainer::stop);
    }

    public static String shareGroupNameFor(DeadLetterTopic topic) {
        return "codely." + topic.contextName() + SHARE_GROUP_SUFFIX;
    }

    private boolean hasSubscriberBean(DomainEventShareGroup shareGroup) {
        return invoker.hasSubscriber(shareGroup.subscriberClass());
    }

    private ShareKafkaMessageListenerContainer<String, String> containerFor(
        DeadLetterTopic topic,
        BiConsumer<DeadLetterTopic, DeadLetterResolution> onResolved
    ) {
        ContainerProperties properties = new ContainerProperties(topic.name());

        properties.setGroupId(shareGroupNameFor(topic));
        properties.setShareAckMode(ContainerProperties.ShareAckMode.MANUAL);
        properties.setMessageListener(new KafkaDeadLetterShareConsumer(topic, replayer, onResolved));

        ShareKafkaMessageListenerContainer<String, String> container = new ShareKafkaMessageListenerContainer<>(
            consumerFactory,
            properties
        );

        container.setBeanName(shareGroupNameFor(topic));

        return container;
    }
}
