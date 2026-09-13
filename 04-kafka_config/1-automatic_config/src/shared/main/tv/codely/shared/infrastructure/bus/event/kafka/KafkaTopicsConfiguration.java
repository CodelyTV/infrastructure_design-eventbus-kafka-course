package tv.codely.shared.infrastructure.bus.event.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import tv.codely.shared.infrastructure.config.Parameter;
import tv.codely.shared.infrastructure.config.ParameterNotExist;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicsConfiguration {
    private static final int OPERATION_TIMEOUT_IN_SECONDS = 5;

    private final Parameter         config;
    private final DomainEventTopics topics;

    public KafkaTopicsConfiguration(Parameter config, DomainEventTopics topics) {
        this.config = config;
        this.topics = topics;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() throws ParameterNotExist {
        Map<String, Object> settings = new HashMap<>();

        settings.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.get("KAFKA_BOOTSTRAP_SERVERS"));

        KafkaAdmin admin = new KafkaAdmin(settings);

        admin.setOperationTimeout(OPERATION_TIMEOUT_IN_SECONDS);

        return admin;
    }

    @Bean
    public KafkaAdmin.NewTopics domainEventsTopics() throws ParameterNotExist {
        int partitions        = config.getInt("KAFKA_TOPICS_PARTITIONS");
        int replicationFactor = config.getInt("KAFKA_TOPICS_REPLICATION_FACTOR");

        NewTopic[] newTopics = topics.all()
                                     .stream()
                                     .map(topic -> TopicBuilder.name(topic.name())
                                                               .partitions(partitions)
                                                               .replicas(replicationFactor)
                                                               .configs(topic.configs())
                                                               .build())
                                     .toArray(NewTopic[]::new);

        return new KafkaAdmin.NewTopics(newTopics);
    }
}
