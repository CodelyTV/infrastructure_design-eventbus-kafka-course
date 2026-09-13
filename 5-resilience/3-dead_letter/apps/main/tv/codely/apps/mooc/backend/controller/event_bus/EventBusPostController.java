package tv.codely.apps.mooc.backend.controller.event_bus;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import tv.codely.shared.domain.DomainError;
import tv.codely.shared.domain.bus.command.CommandBus;
import tv.codely.shared.domain.bus.query.QueryBus;
import tv.codely.shared.infrastructure.bus.event.kafka.KafkaEventBus;
import tv.codely.shared.infrastructure.spring.ApiController;

@RestController
public final class EventBusPostController extends ApiController {

	private final KafkaEventBus eventBus;

	public EventBusPostController(QueryBus queryBus, CommandBus commandBus, KafkaEventBus eventBus) {
		super(queryBus, commandBus);
		this.eventBus = eventBus;
	}

	@PostMapping(value = "/event-bus/republish-failover-events")
	public ResponseEntity<String> index() {
		eventBus.publishFromFailover();

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Override
	public HashMap<Class<? extends DomainError>, HttpStatus> errorMapping() {
		return null;
	}
}
