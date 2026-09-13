package tv.codely.shared.infrastructure.bus.event.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultShareConsumerFactory;
import org.springframework.kafka.core.ShareConsumerFactory;
import tv.codely.shared.infrastructure.config.Parameter;
import tv.codely.shared.infrastructure.config.ParameterNotExist;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaShareConsumersConfiguration {
    private final Parameter config;

    public KafkaShareConsumersConfiguration(Parameter config) {
        this.config = config;
    }

    @Bean
    public ShareConsumerFactory<String, String> domainEventsShareConsumerFactory() throws ParameterNotExist {
        Map<String, Object> settings = new HashMap<>();

        settings.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.get("KAFKA_BOOTSTRAP_SERVERS"));
        settings.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        settings.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultShareConsumerFactory<>(settings);
    }
}
