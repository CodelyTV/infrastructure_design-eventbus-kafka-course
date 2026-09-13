package tv.codely.mooc.courses.domain;

import java.util.UUID;

public record CourseId(String value) {
    public CourseId {
        UUID.fromString(value);
    }
}
