package tv.codely.shared.infrastructure.bus.event.kafka;

import tv.codely.shared.domain.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public final class KafkaTopicsTerraformGenerator {
    private static final String HEADER = "# Generated from the domain events by domain-events:kafka:terraform:generate. Do not edit by hand.\n";

    public String generate(List<? extends KafkaTopic> topics) {
        return topics.stream().map(this::resource).collect(Collectors.joining("\n", HEADER + "\n", ""));
    }

    private String resource(KafkaTopic topic) {
        return String.format(
            """
            resource "kafka_topic" "%s" {
              name               = "%s"
              partitions         = var.partitions
              replication_factor = var.replication_factor

              config = {
            %s
              }
            }
            """,
            topic.terraformResourceName(),
            topic.name(),
            configBlock(topic)
        );
    }

    private String configBlock(KafkaTopic topic) {
        Map<String, String> entries = new TreeMap<>();

        topic.configs().forEach((key, value) -> entries.put(key, String.format("\"%s\"", value)));
        entries.put("min.insync.replicas", "var.min_insync_replicas");

        int keyWidth = entries.keySet().stream().mapToInt(key -> key.length() + 2).max().orElse(0);

        return entries.entrySet()
                      .stream()
                      .map(entry -> String.format("    %-" + keyWidth + "s = %s", quote(entry.getKey()), entry.getValue()))
                      .collect(Collectors.joining("\n"));
    }

    private String quote(String value) {
        return String.format("\"%s\"", value);
    }
}
