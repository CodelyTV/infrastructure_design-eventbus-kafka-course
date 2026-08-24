package tv.codely.apps.backoffice.frontend.controller.courses;

import java.io.Serializable;
import java.util.HashMap;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import tv.codely.mooc.courses.application.create.CourseCreator;
import tv.codely.mooc.courses.domain.CourseDuration;
import tv.codely.mooc.courses.domain.CourseId;
import tv.codely.mooc.courses.domain.CourseName;
import tv.codely.shared.infrastructure.validation.ValidationResponse;
import tv.codely.shared.infrastructure.validation.Validator;

@Controller
public final class CoursesPostWebController {

	private final CourseCreator creator;
	private final HashMap<String, String> rules = new HashMap<String, String>() {
		{
			put("id", "required|not_empty|uuid");
			put("name", "required|not_empty|string");
			put("duration", "required|not_empty|string");
		}
	};

	public CoursesPostWebController(CourseCreator creator) {
		this.creator = creator;
	}

	@PostMapping(value = "/courses", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public RedirectView index(@RequestParam HashMap<String, Serializable> request, RedirectAttributes attributes)
		throws Exception {
		ValidationResponse validationResponse = Validator.validate(request, rules);

		return validationResponse.hasErrors()
			? redirectWithErrors(validationResponse, request, attributes)
			: createCourse(request);
	}

	private RedirectView redirectWithErrors(
		ValidationResponse validationResponse,
		HashMap<String, Serializable> request,
		RedirectAttributes attributes
	) {
		attributes.addFlashAttribute("errors", validationResponse.errors());
		attributes.addFlashAttribute("inputs", request);

		return new RedirectView("/courses");
	}

	private RedirectView createCourse(HashMap<String, Serializable> request) {
		creator.create(
			new CourseId(request.get("id").toString()),
			new CourseName(request.get("name").toString()),
			new CourseDuration(request.get("duration").toString())
		);

		return new RedirectView("/courses");
	}
}
