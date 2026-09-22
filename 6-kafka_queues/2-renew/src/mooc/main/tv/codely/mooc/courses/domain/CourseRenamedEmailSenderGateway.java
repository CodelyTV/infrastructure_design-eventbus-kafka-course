package tv.codely.mooc.courses.domain;

public interface CourseRenamedEmailSenderGateway {
    void send(CourseId id, CourseName name);
}
