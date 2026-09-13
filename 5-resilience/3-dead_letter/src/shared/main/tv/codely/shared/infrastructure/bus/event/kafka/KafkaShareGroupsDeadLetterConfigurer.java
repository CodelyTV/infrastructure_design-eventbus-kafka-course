package tv.codely.shared.infrastructure.bus.event.kafka;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.kafka.core.KafkaAdmin;
import tv.codely.shared.domain.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public final class KafkaShareGroupsDeadLetterConfigurer {
    private static final String DEAD_LETTER_TOPIC_NAME_CONFIG  = "errors.deadletterqueue.topic.name";
    private static final String DEAD_LETTER_COPY_RECORD_CONFIG = "errors.deadletterqueue.copy.record.enable";
    private static final String AUTO_OFFSET_RESET_CONFIG       = "share.auto.offset.reset";

    private final KafkaAdmin admin;

    public KafkaShareGroupsDeadLetterConfigurer(KafkaAdmin admin) {
        this.admin = admin;
    }

    public void configure(List<DomainEventShareGroup> shareGroups) {
        alter(shareGroups.stream().collect(Collectors.toMap(this::resourceFor, this::deadLetterConfigsFor)));
    }

    public void configureDeadLetterConsumers(List<String> shareGroupNames) {
        alter(
            shareGroupNames.stream()
                           .collect(Collectors.toMap(
                               name -> new ConfigResource(ConfigResource.Type.GROUP, name),
                               name -> List.of(set(AUTO_OFFSET_RESET_CONFIG, "earliest"))
                           ))
        );
    }

    private void alter(Map<ConfigResource, Collection<AlterConfigOp>> configs) {
        try (Admin client = Admin.create(admin.getConfigurationProperties())) {
            client.incrementalAlterConfigs(configs).all().get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Could not configure the dead letter topics of the share groups", exception);
        }
    }

    private ConfigResource resourceFor(DomainEventShareGroup shareGroup) {
        return new ConfigResource(ConfigResource.Type.GROUP, shareGroup.name());
    }

    private List<AlterConfigOp> deadLetterConfigsFor(DomainEventShareGroup shareGroup) {
        return List.of(
            set(DEAD_LETTER_TOPIC_NAME_CONFIG, shareGroup.deadLetterTopic().name()),
            set(DEAD_LETTER_COPY_RECORD_CONFIG, "true")
        );
    }

    private AlterConfigOp set(String name, String value) {
        return new AlterConfigOp(new ConfigEntry(name, value), AlterConfigOp.OpType.SET);
    }
}
