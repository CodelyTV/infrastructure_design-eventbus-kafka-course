package tv.codely.apps.backoffice.backend.command;

import java.util.List;

import tv.codely.shared.domain.bus.event.DomainEvent;
import tv.codely.shared.infrastructure.bus.event.kafka.DomainEventShareGroup;
import tv.codely.shared.infrastructure.bus.event.kafka.KafkaDomainEventsShareConsumers;
import tv.codely.shared.infrastructure.cli.ConsoleCommand;

public final class ConsumeKafkaDomainEventsCommand extends ConsoleCommand {

	private final KafkaDomainEventsShareConsumers consumers;

	public ConsumeKafkaDomainEventsCommand(KafkaDomainEventsShareConsumers consumers) {
		this.consumers = consumers;
	}

	@Override
	public void execute(String[] args) {
		List<DomainEventShareGroup> started = consumers.start(this::logConsumed);

		log(String.format("Started %d share consumers, one per subscriber:", started.size()));
		started.forEach(this::logStarted);

		Runtime.getRuntime().addShutdownHook(new Thread(consumers::stop));

		try {
			Thread.currentThread().join();
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
		}
	}

	private void logStarted(DomainEventShareGroup shareGroup) {
		info(
			String.format(
				"- Share group <%s> -> subscriber <%s> consumes topics %s",
				shareGroup.name(),
				shareGroup.subscriberClass().getSimpleName(),
				shareGroup.topics()
			)
		);
	}

	private void logConsumed(DomainEventShareGroup shareGroup, DomainEvent event) {
		log(
			String.format(
				"<%s> consumed <%s> with id <%s>",
				shareGroup.subscriberClass().getSimpleName(),
				event.eventName(),
				event.eventId()
			)
		);
	}
}
