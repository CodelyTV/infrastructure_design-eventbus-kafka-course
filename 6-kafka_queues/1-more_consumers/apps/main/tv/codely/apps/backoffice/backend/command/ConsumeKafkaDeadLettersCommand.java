package tv.codely.apps.backoffice.backend.command;

import java.util.List;

import tv.codely.shared.infrastructure.bus.event.kafka.DeadLetterMessage;
import tv.codely.shared.infrastructure.bus.event.kafka.DeadLetterResolution;
import tv.codely.shared.infrastructure.bus.event.kafka.DeadLetterTopic;
import tv.codely.shared.infrastructure.bus.event.kafka.KafkaDeadLetterShareConsumers;
import tv.codely.shared.infrastructure.cli.ConsoleCommand;

public final class ConsumeKafkaDeadLettersCommand extends ConsoleCommand {

	private final KafkaDeadLetterShareConsumers consumers;

	public ConsumeKafkaDeadLettersCommand(KafkaDeadLetterShareConsumers consumers) {
		this.consumers = consumers;
	}

	@Override
	public void execute(String[] args) {
		List<DeadLetterTopic> started = consumers.start(this::logResolved);

		log(String.format("Started %d dead letter consumers, one per context:", started.size()));
		started.forEach(this::logStarted);

		Runtime.getRuntime().addShutdownHook(new Thread(consumers::stop));

		try {
			Thread.currentThread().join();
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
		}
	}

	private void logStarted(DeadLetterTopic topic) {
		info(
			String.format(
				"- Share group <%s> consumes dead letter topic <%s>",
				KafkaDeadLetterShareConsumers.shareGroupNameFor(topic),
				topic.name()
			)
		);
	}

	private void logResolved(DeadLetterTopic topic, DeadLetterResolution resolution) {
		DeadLetterMessage message = resolution.message();

		error(
			String.format(
				"Dead letter in <%s> from <%s-%d@%d> failed in share group <%s> after %s deliveries: %s",
				topic.name(),
				message.originalTopic(),
				message.originalPartition(),
				message.originalOffset(),
				message.failedShareGroup(),
				message.deliveryCount().map(String::valueOf).orElse("?"),
				message.cause()
			)
		);
		info(String.format("  key: %s", message.key()));
		info(String.format("  body: %s", message.body()));
		log(String.format("  -> %s: %s", resolution.action(), resolution.reason()));
	}
}
