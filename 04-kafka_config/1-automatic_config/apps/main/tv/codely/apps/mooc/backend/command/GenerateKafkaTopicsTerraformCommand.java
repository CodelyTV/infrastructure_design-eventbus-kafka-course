package tv.codely.apps.mooc.backend.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import tv.codely.shared.infrastructure.bus.event.kafka.DomainEventTopics;
import tv.codely.shared.infrastructure.bus.event.kafka.KafkaTopicsTerraformGenerator;
import tv.codely.shared.infrastructure.cli.ConsoleCommand;

public final class GenerateKafkaTopicsTerraformCommand extends ConsoleCommand {

	public static final Path DEFAULT_OUTPUT = Path.of("etc/terraform/kafka/topics.tf");

	private final DomainEventTopics topics;
	private final KafkaTopicsTerraformGenerator generator;

	public GenerateKafkaTopicsTerraformCommand(DomainEventTopics topics, KafkaTopicsTerraformGenerator generator) {
		this.topics = topics;
		this.generator = generator;
	}

	@Override
	public void execute(String[] args) {
		Path output = args.length > 0 ? Path.of(args[0]) : DEFAULT_OUTPUT;

		try {
			Files.createDirectories(output.getParent());
			Files.writeString(output, generator.generate(topics.all()));

			log(String.format("Generated %d Kafka topics in %s", topics.all().size(), output));
		} catch (IOException exception) {
			error(String.format("Could not write %s: %s", output, exception.getMessage()));
		}
	}
}
