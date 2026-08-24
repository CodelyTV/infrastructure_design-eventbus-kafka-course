package tv.codely.mooc.shared.infrastructure.persistence;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tv.codely.shared.infrastructure.bus.event.DomainEventsInformation;
import tv.codely.shared.infrastructure.bus.event.postgres.PostgresDomainEventsConsumer;
import tv.codely.shared.infrastructure.bus.event.postgres.PostgresEventBus;
import tv.codely.shared.infrastructure.bus.event.spring.SpringApplicationEventBus;

@Configuration
public class MoocPostgresEventBusConfiguration {
    private final SessionFactory            sessionFactory;
    private final DomainEventsInformation   domainEventsInformation;
    private final SpringApplicationEventBus bus;

    public MoocPostgresEventBusConfiguration(
        @Qualifier("mooc-session_factory") SessionFactory sessionFactory,
        DomainEventsInformation domainEventsInformation,
        SpringApplicationEventBus bus
    ) {
        this.sessionFactory          = sessionFactory;
        this.domainEventsInformation = domainEventsInformation;
        this.bus                     = bus;
    }

    @Bean
    public PostgresEventBus moocPostgresEventBus() {
        return new PostgresEventBus(sessionFactory);
    }

    @Bean
    public PostgresDomainEventsConsumer moocPostgresDomainEventsConsumer() {
        return new PostgresDomainEventsConsumer(sessionFactory, domainEventsInformation, bus);
    }
}
