package tv.codely.mooc.courses_counter.infrastructure.persistence;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import tv.codely.mooc.courses_counter.CoursesCounterModuleInfrastructureTestCase;
import tv.codely.mooc.courses_counter.domain.CoursesCounter;
import tv.codely.mooc.courses_counter.domain.CoursesCounterMother;

import jakarta.transaction.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
class PostgresCoursesCounterRepositoryShould extends CoursesCounterModuleInfrastructureTestCase {
    @Autowired
    @Qualifier("mooc-session_factory")
    private SessionFactory sessionFactory;

    @BeforeEach
    void clearExistingCounters() {
        sessionFactory.getCurrentSession().createNativeMutationQuery("DELETE FROM courses_counter").executeUpdate();
    }

    @Test
    void return_an_existing_courses_counter() {
        CoursesCounter counter = CoursesCounterMother.random();

        repository.save(counter);

        assertEquals(Optional.of(counter), repository.search());
    }
}
