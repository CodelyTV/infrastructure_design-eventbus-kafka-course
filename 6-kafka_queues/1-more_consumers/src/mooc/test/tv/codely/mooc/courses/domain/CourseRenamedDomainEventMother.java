package tv.codely.mooc.courses.domain;

import tv.codely.shared.domain.course.CourseRenamedDomainEvent;

public final class CourseRenamedDomainEventMother {
    public static CourseRenamedDomainEvent create(CourseId id, CourseName name) {
        return new CourseRenamedDomainEvent(id.value(), name.value());
    }

    public static CourseRenamedDomainEvent random() {
        return create(CourseIdMother.random(), CourseNameMother.random());
    }
}
