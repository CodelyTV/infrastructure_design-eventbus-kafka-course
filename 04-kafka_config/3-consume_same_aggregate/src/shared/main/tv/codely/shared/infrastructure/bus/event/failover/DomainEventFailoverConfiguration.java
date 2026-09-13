package tv.codely.shared.infrastructure.bus.event.failover;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import tv.codely.shared.infrastructure.config.Parameter;
import tv.codely.shared.infrastructure.config.ParameterNotExist;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

@Configuration
public class DomainEventFailoverConfiguration {
    private final Parameter               config;
    private final ResourcePatternResolver resourceResolver;

    public DomainEventFailoverConfiguration(Parameter config, ResourcePatternResolver resourceResolver) {
        this.config           = config;
        this.resourceResolver = resourceResolver;
    }

    @Bean("failover-data_source")
    public DataSource failoverDataSource() throws ParameterNotExist, IOException, SQLException {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(
            String.format(
                "jdbc:postgresql://%s:%s/%s",
                config.get("FAILOVER_DATABASE_HOST"),
                config.getInt("FAILOVER_DATABASE_PORT"),
                config.get("FAILOVER_DATABASE_NAME")
            )
        );
        dataSource.setUsername(config.get("FAILOVER_DATABASE_USER"));
        dataSource.setPassword(config.get("FAILOVER_DATABASE_PASSWORD"));
        dataSource.setConnectionInitSqls(new ArrayList<>(Arrays.asList(schemaSentences().split(";"))));

        try (Connection connection = dataSource.getConnection()) {
            connection.isValid(1);
        }

        return dataSource;
    }

    @Bean
    public DomainEventFailover domainEventFailover() throws ParameterNotExist, IOException, SQLException {
        return new DomainEventFailover(failoverDataSource());
    }

    private String schemaSentences() throws IOException {
        Resource schema = resourceResolver.getResource("classpath:database/shared.sql");

        return new Scanner(schema.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
    }
}
