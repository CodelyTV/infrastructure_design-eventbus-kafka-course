package tv.codely.apps.mooc.backend.command;

import tv.codely.shared.infrastructure.bus.event.postgres.PostgresDomainEventsConsumer;
import tv.codely.shared.infrastructure.cli.ConsoleCommand;

public final class ConsumePostgresDomainEventsCommand extends ConsoleCommand {

	private final PostgresDomainEventsConsumer consumer;

	public ConsumePostgresDomainEventsCommand(PostgresDomainEventsConsumer consumer) {
		this.consumer = consumer;
	}

	@Override
	public void execute(String[] args) {
		consumer.consume();
	}
}
