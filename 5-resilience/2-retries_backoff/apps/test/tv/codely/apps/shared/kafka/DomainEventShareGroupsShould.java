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
		assertTrue(names.contains("codely.mooc.courses.log-course-events-on-course-events"));
	}

	@Test
	void subscribe_each_share_group_to_the_topics_of_its_events() {
		DomainEventShareGroup shareGroup = shareGroupNamed(
			"codely.mooc.courses_counter.increment-courses-counter-on-course-created"
		);

		assertEquals(List.of("codely.mooc.course.created"), shareGroup.topics());
	}

	@Test
	void subscribe_a_share_group_to_every_topic_matching_the_event_name_pattern() {
		DomainEventShareGroup shareGroup = shareGroupNamed("codely.mooc.courses.log-course-events-on-course-events");

		assertEquals(List.of("codely.mooc.course.created", "codely.mooc.course.renamed"), shareGroup.topics());
	}

	@Test
	void not_create_a_topic_for_an_event_name_pattern() {
		assertTrue(events.eventNames().stream().noneMatch(name -> name.contains("*")));
	}

	private DomainEventShareGroup shareGroupNamed(String name) {
		return shareGroups.all().stream().filter(group -> group.name().equals(name)).findFirst().orElseThrow();
	}
}
