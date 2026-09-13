package tv.codely.apps.mooc.backend.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import tv.codely.shared.infrastructure.bus.event.kafka.DeadLetterTopics;
import tv.codely.shared.infrastructure.bus.event.kafka.DomainEventTopics;
import tv.codely.shared.infrastructure.bus.event.kafka.KafkaTopic;
import tv.codely.shared.infrastructure.bus.event.kafka.KafkaTopicsTerraformGenerator;
import tv.codely.shared.infrastructure.cli.ConsoleCommand;

public final class GenerateKafkaTopicsTerraformCommand extends ConsoleCommand {

	public static final Path DEFAULT_OUTPUT = Path.of("etc/terraform/kafka/topics.tf");

	private final DomainEventTopics topics;
	private final DeadLetterTopics deadLetterTopics;
	private final KafkaTopicsTerraformGenerator terraform;

	public GenerateKafkaTopicsTerraformCommand(
		DomainEventTopics topics,
		DeadLetterTopics deadLetterTopics,
		KafkaTopicsTerraformGenerator terraform
	) {
		this.topics = topics;
		this.deadLetterTopics = deadLetterTopics;
		this.terraform = terraform;
	}

	public static List<KafkaTopic> allTopics(DomainEventTopics topics, DeadLetterTopics deadLetterTopics) {
		return Stream.concat(topics.all().stream(), deadLetterTopics.all().stream()).map(KafkaTopic.class::cast).toList();
	}

	@Override
	public void execute(String[] args) {
		Path output = args.length > 0 ? Path.of(args[0]) : DEFAULT_OUTPUT;
		List<KafkaTopic> allTopics = allTopics(topics, deadLetterTopics);

		try {
			Files.createDirectories(output.getParent());
			Files.writeString(output, terraform.generate(allTopics));

			log(String.format("Generated %d Kafka topics in %s", allTopics.size(), output));
		} catch (IOException exception) {
			error(String.format("Could not write %s: %s", output, exception.getMessage()));
		}
	}
}
