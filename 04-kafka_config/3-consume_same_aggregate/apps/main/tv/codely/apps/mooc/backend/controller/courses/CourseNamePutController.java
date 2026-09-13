package tv.codely.apps.mooc.backend.controller.courses;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tv.codely.mooc.courses.application.rename.CourseRenamer;
import tv.codely.mooc.courses.domain.CourseNotExist;
import tv.codely.shared.domain.DomainError;
import tv.codely.shared.domain.bus.command.CommandBus;
import tv.codely.shared.domain.bus.query.QueryBus;
import tv.codely.shared.infrastructure.spring.ApiController;

@RestController
public final class CourseNamePutController extends ApiController {

	private final CourseRenamer renamer;

	public CourseNamePutController(QueryBus queryBus, CommandBus commandBus, CourseRenamer renamer) {
		super(queryBus, commandBus);
		this.renamer = renamer;
	}

	@PutMapping(value = "/courses/{id}/name")
	public ResponseEntity<String> index(@PathVariable String id, @RequestBody RenameRequest request)
		throws CourseNotExist {
		renamer.rename(id, request.name());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public HashMap<Class<? extends DomainError>, HttpStatus> errorMapping() {
		return new HashMap<Class<? extends DomainError>, HttpStatus>() {
			{
				put(CourseNotExist.class, HttpStatus.NOT_FOUND);
			}
		};
	}
}

final class RenameRequest {

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	String name() {
		return name;
	}
}
