package tv.codely.shared.infrastructure.bus.event.failover;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class DomainEventFailover {
    private final DataSource dataSource;

    public DomainEventFailover(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void publish(String eventId, String eventName, String body) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO failover_domain_events (event_id, event_name, body) VALUES (CAST(? AS UUID), ?, ?)"
            )
        ) {
            statement.setString(1, eventId);
            statement.setString(2, eventName);
            statement.setString(3, body);

            statement.executeUpdate();
        } catch (SQLException error) {
            throw new IllegalStateException("Cannot store the domain event in the failover", error);
        }
    }

    public List<FailedDomainEvent> consume(int total) {
        List<FailedDomainEvent> events = new ArrayList<>();

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM failover_domain_events " +
                "WHERE id IN (SELECT id FROM failover_domain_events ORDER BY id LIMIT ?) " +
                "RETURNING event_id, event_name, body"
            )
        ) {
            statement.setInt(1, total);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    events.add(new FailedDomainEvent(
                        result.getString("event_id"),
                        result.getString("event_name"),
                        result.getString("body")
                    ));
                }
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Cannot read the domain events from the failover", error);
        }

        return events;
    }
}
