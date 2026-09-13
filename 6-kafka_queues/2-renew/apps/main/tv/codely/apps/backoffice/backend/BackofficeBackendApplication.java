package tv.codely.apps.backoffice.backend;

import java.util.HashMap;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import tv.codely.apps.backoffice.backend.command.ConsumeKafkaDeadLettersCommand;
import tv.codely.apps.backoffice.backend.command.ConsumeKafkaDomainEventsCommand;
import tv.codely.shared.domain.Service;

@SpringBootApplication
@ComponentScan(
	includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class),
	value = { "tv.codely.shared", "tv.codely.backoffice", "tv.codely.apps.backoffice.backend" }
)
public class BackofficeBackendApplication {

	public static HashMap<String, Class<?>> commands() {
		return new HashMap<String, Class<?>>() {
			{
				put("domain-events:kafka:consume", ConsumeKafkaDomainEventsCommand.class);
				put("domain-events:kafka:dead-letter:consume", ConsumeKafkaDeadLettersCommand.class);
			}
		};
	}
}
