package tv.codely.apps.mooc.backend.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import tv.codely.shared.infrastructure.bus.event.DomainEventsInformation;
import tv.codely.shared.infrastructure.bus.event.kafka.DomainEventTopics;
import tv.codely.shared.infrastructure.bus.event.kafka.KafkaTopicsTerraformGenerator;

final class GenerateKafkaTopicsTerraformCommandShould {

	@Test
	void keep_the_terraform_topics_in_sync_with_the_domain_events() throws IOException {
		DomainEventTopics topics = new DomainEventTopics(new DomainEventsInformation());

		String expected = new KafkaTopicsTerraformGenerator().generate(topics.all());
		String current = Files.readString(GenerateKafkaTopicsTerraformCommand.DEFAULT_OUTPUT);

		assertEquals(
			expected,
			current,
			"The Kafka topics Terraform file is outdated. Run `make generate-kafka-terraform`."
		);
	}
}
