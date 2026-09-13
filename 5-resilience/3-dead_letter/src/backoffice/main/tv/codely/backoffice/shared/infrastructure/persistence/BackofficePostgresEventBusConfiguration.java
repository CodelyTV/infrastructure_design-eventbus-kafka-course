package tv.codely.backoffice.shared.infrastructure.persistence;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tv.codely.shared.infrastructure.bus.event.DomainEventsInformation;
import tv.codely.shared.infrastructure.bus.event.postgres.PostgresDomainEventsConsumer;
import tv.codely.shared.infrastructure.bus.event.postgres.PostgresEventBus;
import tv.codely.shared.infrastructure.bus.event.spring.SpringApplicationEventBus;

@Configuration
public class BackofficePostgresEventBusConfiguration {
    private final SessionFactory            sessionFactory;
    private final DomainEventsInformation   domainEventsInformation;
    private final SpringApplicationEventBus bus;

    public BackofficePostgresEventBusConfiguration(
        @Qualifier("backoffice-session_factory") SessionFactory sessionFactory,
        DomainEventsInformation domainEventsInformation,
        SpringApplicationEventBus bus
    ) {
        this.sessionFactory          = sessionFactory;
        this.domainEventsInformation = domainEventsInformation;
        this.bus                     = bus;
    }

    @Bean
    public PostgresEventBus backofficePostgresEventBus() {
        return new PostgresEventBus(sessionFactory);
    }

    @Bean
    public PostgresDomainEventsConsumer backofficePostgresDomainEventsConsumer() {
        return new PostgresDomainEventsConsumer(sessionFactory, domainEventsInformation, bus);
    }
}
