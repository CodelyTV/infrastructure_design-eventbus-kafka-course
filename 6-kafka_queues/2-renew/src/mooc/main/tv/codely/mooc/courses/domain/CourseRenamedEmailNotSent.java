package tv.codely.mooc.courses.domain;

import tv.codely.shared.domain.DomainError;

public final class CourseRenamedEmailNotSent extends DomainError {
    public CourseRenamedEmailNotSent(CourseId id, String reason) {
        super(
            "course_renamed_email_not_sent",
            String.format("The email for the renamed course <%s> could not be sent: %s", id.value(), reason)
        );
    }
}
