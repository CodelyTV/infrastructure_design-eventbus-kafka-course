package tv.codely.mooc.shared.infrastructure.bus.event.postgres;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tv.codely.mooc.MoocContextInfrastructureTestCase;
import tv.codely.mooc.courses.domain.CourseCreatedDomainEventMother;
import tv.codely.shared.domain.course.CourseCreatedDomainEvent;
import tv.codely.shared.infrastructure.bus.event.postgres.PostgresDomainEventsConsumer;
import tv.codely.shared.infrastructure.bus.event.postgres.PostgresEventBus;

import jakarta.transaction.Transactional;
import java.util.Collections;

@Transactional
class PostgresEventBusShould extends MoocContextInfrastructureTestCase {
    @Autowired
    private PostgresEventBus             eventBus;
    @Autowired
    private PostgresDomainEventsConsumer consumer;

    @Test
    void publish_and_consume_domain_events_from_postgres() throws InterruptedException {
        CourseCreatedDomainEvent domainEvent = CourseCreatedDomainEventMother.random();

        eventBus.publish(Collections.singletonList(domainEvent));

        Thread consumerProcess = new Thread(() -> consumer.consume());
        consumerProcess.start();

        Thread.sleep(100);

        consumer.stop();
    }
}
