package tv.codely.mooc.courses.application.rename;

import tv.codely.mooc.courses.domain.Course;
import tv.codely.mooc.courses.domain.CourseId;
import tv.codely.mooc.courses.domain.CourseName;
import tv.codely.mooc.courses.domain.CourseNotExist;
import tv.codely.mooc.courses.domain.CourseRepository;
import tv.codely.shared.domain.Service;
import tv.codely.shared.domain.bus.event.EventBus;

@Service
public final class CourseRenamer {
    private final CourseRepository repository;
    private final EventBus         eventBus;

    public CourseRenamer(CourseRepository repository, EventBus eventBus) {
        this.repository = repository;
        this.eventBus   = eventBus;
    }

    public void rename(String id, String name) throws CourseNotExist {
        CourseId courseId = new CourseId(id);
        Course   course   = repository.search(courseId).orElseThrow(() -> new CourseNotExist(courseId));

        course.rename(new CourseName(name));

        repository.save(course);
        eventBus.publish(course.pullDomainEvents());
    }
}
