package tv.codely.apps.mooc.backend.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import tv.codely.shared.infrastructure.bus.event.DomainEventSubscribersInformation;
import tv.codely.shared.infrastructure.bus.event.DomainEventsInformation;
import tv.codely.shared.infrastructure.bus.event.kafka.DeadLetterTopics;
import tv.codely.shared.infrastructure.bus.event.kafka.DomainEventShareGroups;
import tv.codely.shared.infrastructure.bus.event.kafka.DomainEventTopics;
import tv.codely.shared.infrastructure.bus.event.kafka.KafkaTopicsTerraformGenerator;

final class GenerateKafkaTopicsTerraformCommandShould {

	@Test
	void keep_the_terraform_topics_in_sync_with_the_domain_events() throws IOException {
		DomainEventsInformation events = new DomainEventsInformation();
		DomainEventTopics topics = new DomainEventTopics(events);
		DeadLetterTopics deadLetterTopics = new DeadLetterTopics(
			new DomainEventShareGroups(new DomainEventSubscribersInformation(), events)
		);

		String expected = new KafkaTopicsTerraformGenerator()
			.generate(GenerateKafkaTopicsTerraformCommand.allTopics(topics, deadLetterTopics));
		String current = Files.readString(GenerateKafkaTopicsTerraformCommand.DEFAULT_OUTPUT);

		assertEquals(
			expected,
			current,
			"The Kafka topics Terraform file is outdated. Run `make generate-kafka-terraform`."
		);
	}
}
