package tv.codely.mooc.courses.application.rename;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tv.codely.mooc.courses.CoursesModuleUnitTestCase;
import tv.codely.mooc.courses.domain.Course;
import tv.codely.mooc.courses.domain.CourseId;
import tv.codely.mooc.courses.domain.CourseIdMother;
import tv.codely.mooc.courses.domain.CourseMother;
import tv.codely.mooc.courses.domain.CourseName;
import tv.codely.mooc.courses.domain.CourseNameMother;
import tv.codely.mooc.courses.domain.CourseNotExist;
import tv.codely.mooc.courses.domain.CourseRenamedDomainEventMother;
import tv.codely.shared.domain.course.CourseRenamedDomainEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class CourseRenamerShould extends CoursesModuleUnitTestCase {
    private CourseRenamer renamer;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        renamer = new CourseRenamer(repository, eventBus);
    }

    @Test
    void rename_an_existing_course() throws CourseNotExist {
        Course                   course      = CourseMother.random();
        CourseName               newName     = CourseNameMother.random();
        Course                   renamed     = CourseMother.create(course.id(), newName, course.duration());
        CourseRenamedDomainEvent domainEvent = CourseRenamedDomainEventMother.create(course.id(), newName);

        shouldSearch(course.id(), course);

        renamer.rename(course.id().value(), newName.value());

        shouldHaveSaved(renamed);
        shouldHavePublished(domainEvent);
    }

    @Test
    void throw_an_exception_when_the_course_does_not_exist() {
        CourseId id = CourseIdMother.random();

        shouldNotSearch(id);

        assertThrows(CourseNotExist.class, () -> renamer.rename(id.value(), CourseNameMother.random().value()));
    }
}
