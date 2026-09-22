package tv.codely.apps.mooc.backend.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import tv.codely.shared.infrastructure.bus.event.kafka.DomainEventTopic;
import tv.codely.shared.infrastructure.bus.event.kafka.DomainEventTopics;
import tv.codely.shared.infrastructure.cli.ConsoleCommand;

public final class GenerateKafkaTopicsTerraformCommand extends ConsoleCommand {

	public static final Path DEFAULT_OUTPUT = Path.of("etc/terraform/kafka/topics.tf");

	private static final String HEADER = "# Generated from the domain events by domain-events:kafka:terraform:generate. Do not edit by hand.\n";

	private final DomainEventTopics topics;

	public GenerateKafkaTopicsTerraformCommand(DomainEventTopics topics) {
		this.topics = topics;
	}

	@Override
	public void execute(String[] args) {
		Path output = args.length > 0 ? Path.of(args[0]) : DEFAULT_OUTPUT;

		try {
			Files.createDirectories(output.getParent());
			Files.writeString(output, generate(topics.all()));

			log(String.format("Generated %d Kafka topics in %s", topics.all().size(), output));
		} catch (IOException exception) {
			error(String.format("Could not write %s: %s", output, exception.getMessage()));
		}
	}

	static String generate(List<DomainEventTopic> topics) {
		return topics.stream().map(GenerateKafkaTopicsTerraformCommand::resource).collect(Collectors.joining("\n", HEADER + "\n", ""));
	}

	private static String resource(DomainEventTopic topic) {
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

	private static String configBlock(DomainEventTopic topic) {
		Map<String, String> entries = new TreeMap<>();

		topic.configs().forEach((key, value) -> entries.put(key, String.format("\"%s\"", value)));
		entries.put("min.insync.replicas", "var.min_insync_replicas");

		int keyWidth = entries.keySet().stream().mapToInt(key -> key.length() + 2).max().orElse(0);

		return entries.entrySet()
					  .stream()
					  .map(entry -> String.format("    %-" + keyWidth + "s = %s", quote(entry.getKey()), entry.getValue()))
					  .collect(Collectors.joining("\n"));
	}

	private static String quote(String value) {
		return String.format("\"%s\"", value);
	}
}
