package tv.codely.apps.mooc.backend.controller.courses;

import org.junit.jupiter.api.Test;

import tv.codely.apps.mooc.MoocApplicationTestCase;

public final class CourseNamePutControllerShould extends MoocApplicationTestCase {

	@Test
	void rename_an_existing_course() throws Exception {
		String id = "6a1cb7d3-2b6e-4c0f-9a51-0f4c3c2d8e10";

		givenThereIsACourse(id);

		assertRequestWithBody("PUT", String.format("/courses/%s/name", id), "{\"name\": \"The renamed course\"}", 200);

		assertResponse(
			String.format("/courses/%s", id),
			200,
			String.format("{\"id\": \"%s\", \"name\": \"The renamed course\", \"duration\": \"5 hours\"}", id)
		);
	}

	@Test
	void not_rename_a_non_existing_course() throws Exception {
		String id = "aa1cb7d3-2b6e-4c0f-9a51-0f4c3c2d8e99";
		String expectedResponse = String.format(
			"{\"error_code\": \"course_not_exist\", \"message\": \"The course <%s> doesn't exist\"}",
			id
		);

		assertRequestWithBody(
			"PUT",
			String.format("/courses/%s/name", id),
			"{\"name\": \"The renamed course\"}",
			404,
			expectedResponse
		);
	}

	private void givenThereIsACourse(String id) throws Exception {
		assertRequestWithBody(
			"PUT",
			String.format("/courses/%s", id),
			"{\"name\": \"The best course\", \"duration\": \"5 hours\"}",
			201
		);
	}
}
