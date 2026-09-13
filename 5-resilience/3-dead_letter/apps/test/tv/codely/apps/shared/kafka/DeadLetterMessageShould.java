package tv.codely.apps.shared.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import tv.codely.shared.infrastructure.bus.event.kafka.DeadLetterMessage;

final class DeadLetterMessageShould {

	@Test
	void read_the_origin_and_the_cause_from_the_headers_written_by_the_broker() {
		ConsumerRecord<String, String> record = new ConsumerRecord<>("dlq.codely.mooc", 0, 7L, "key", "body");

		header(record, "__dlq.errors.topic", "codely.mooc.course.created");
		header(record, "__dlq.errors.partition", "2");
		header(record, "__dlq.errors.offset", "41");
		header(record, "__dlq.errors.group", "codely.mooc.courses_counter.increment-courses-counter-on-course-created");
		header(record, "__dlq.errors.delivery.count", "3");
		header(record, "__dlq.errors.message", "Offset delivery count exceeded the threshold.");

		DeadLetterMessage message = DeadLetterMessage.fromRecord(record);

		assertEquals("codely.mooc.course.created", message.originalTopic());
		assertEquals(2, message.originalPartition());
		assertEquals(41L, message.originalOffset());
		assertEquals("codely.mooc.courses_counter.increment-courses-counter-on-course-created", message.failedShareGroup());
		assertEquals(Optional.of((short) 3), message.deliveryCount());
		assertEquals("key", message.key());
		assertEquals("body", message.body());
		assertTrue(message.exceededDeliveryCount());
		assertFalse(message.wasRejectedByClient());
	}

	@Test
	void detect_a_record_rejected_by_the_client() {
		ConsumerRecord<String, String> record = new ConsumerRecord<>("dlq.codely.mooc", 0, 0L, "key", "{broken");

		header(record, "__dlq.errors.message", "Offset rejected by client.");

		DeadLetterMessage message = DeadLetterMessage.fromRecord(record);

		assertTrue(message.wasRejectedByClient());
		assertEquals(Optional.empty(), message.deliveryCount());
	}

	private void header(ConsumerRecord<String, String> record, String name, String value) {
		record.headers().add(name, value.getBytes(StandardCharsets.UTF_8));
	}
}
