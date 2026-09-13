package tv.codely.mooc.courses;

import org.junit.jupiter.api.BeforeEach;
import tv.codely.mooc.courses.domain.Course;
import tv.codely.mooc.courses.domain.CourseId;
import tv.codely.mooc.courses.domain.CourseRepository;
import tv.codely.shared.infrastructure.UnitTestCase;

import java.util.Optional;

import static org.mockito.Mockito.*;

public abstract class CoursesModuleUnitTestCase extends UnitTestCase {
    protected CourseRepository repository;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        repository = mock(CourseRepository.class);
    }

    public void shouldHaveSaved(Course course) {
        verify(repository, atLeastOnce()).save(course);
    }

    public void shouldSearch(CourseId id, Course course) {
        when(repository.search(id)).thenReturn(Optional.of(course));
    }

    public void shouldNotSearch(CourseId id) {
        when(repository.search(id)).thenReturn(Optional.empty());
    }
}
