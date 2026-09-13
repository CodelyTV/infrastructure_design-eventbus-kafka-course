package tv.codely.apps.shared.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import tv.codely.shared.infrastructure.bus.event.DomainEventSubscribersInformation;
import tv.codely.shared.infrastructure.bus.event.DomainEventsInformation;
import tv.codely.shared.infrastructure.bus.event.kafka.DomainEventShareGroup;
import tv.codely.shared.infrastructure.bus.event.kafka.DomainEventShareGroups;

final class DomainEventShareGroupsShould {

	private final DomainEventsInformation events = new DomainEventsInformation();
	private final DomainEventShareGroups shareGroups = new DomainEventShareGroups(
		new DomainEventSubscribersInformation(),
		events
	);

	@Test
	void create_one_share_group_per_subscriber_named_after_it() {
		List<String> names = shareGroups.all().stream().map(DomainEventShareGroup::name).toList();

		assertTrue(names.contains("codely.backoffice.courses.create-backoffice-course-on-course-created"));
		assertTrue(names.contains("codely.mooc.courses_counter.increment-courses-counter-on-course-created"));
	}

	@Test
	void subscribe_each_share_group_to_the_topics_of_its_events() {
		DomainEventShareGroup shareGroup = shareGroupNamed(
			"codely.mooc.courses_counter.increment-courses-counter-on-course-created"
		);

		assertEquals(List.of("codely.mooc.course.created"), shareGroup.topics());
	}

	private DomainEventShareGroup shareGroupNamed(String name) {
		return shareGroups.all().stream().filter(group -> group.name().equals(name)).findFirst().orElseThrow();
	}
}
