package tv.codely.shared.infrastructure.bus.event.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import tv.codely.shared.infrastructure.bus.event.failover.DomainEventFailover;
import tv.codely.shared.infrastructure.config.Parameter;
import tv.codely.shared.infrastructure.config.ParameterNotExist;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaEventBusConfiguration {
    private final Parameter           config;
    private final DomainEventFailover failover;

    public KafkaEventBusConfiguration(Parameter config, DomainEventFailover failover) {
        this.config   = config;
        this.failover = failover;
    }

    @Bean
    public ProducerFactory<String, String> domainEventsProducerFactory() throws ParameterNotExist {
        Map<String, Object> settings = new HashMap<>();

        settings.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.get("KAFKA_BOOTSTRAP_SERVERS"));
        settings.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        settings.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        settings.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, config.getInt("KAFKA_MAX_BLOCK_MS"));

        return new DefaultKafkaProducerFactory<>(settings);
    }

    @Bean
    public KafkaTemplate<String, String> domainEventsKafkaTemplate() throws ParameterNotExist {
        return new KafkaTemplate<>(domainEventsProducerFactory());
    }

    @Primary
    @Bean
    public KafkaEventBus kafkaEventBus() throws ParameterNotExist {
        return new KafkaEventBus(domainEventsKafkaTemplate(), config.get("KAFKA_DOMAIN_EVENTS_TOPIC"), failover);
    }
}
