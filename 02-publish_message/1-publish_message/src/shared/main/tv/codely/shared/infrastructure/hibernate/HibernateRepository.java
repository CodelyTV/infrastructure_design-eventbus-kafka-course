package tv.codely.shared.infrastructure.hibernate;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.SessionFactory;
import tv.codely.shared.domain.criteria.Criteria;

import java.util.List;
import java.util.Optional;

public abstract class HibernateRepository<T> {
    protected final SessionFactory             sessionFactory;
    protected final Class<T>                   aggregateClass;
    protected final HibernateCriteriaConverter<T> criteriaConverter;

    public HibernateRepository(SessionFactory sessionFactory, Class<T> aggregateClass) {
        this.sessionFactory    = sessionFactory;
        this.aggregateClass    = aggregateClass;
        this.criteriaConverter = new HibernateCriteriaConverter<>(sessionFactory.getCriteriaBuilder());
    }

    protected void persist(T entity) {
        sessionFactory.getCurrentSession().merge(entity);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
    }

    protected Optional<T> byId(Object id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().find(aggregateClass, id));
    }

    protected List<T> byCriteria(Criteria criteria) {
        CriteriaQuery<T> hibernateCriteria = criteriaConverter.convert(criteria, aggregateClass);

        return sessionFactory.getCurrentSession().createQuery(hibernateCriteria).getResultList();
    }

    protected List<T> all() {
        CriteriaQuery<T> criteria = sessionFactory.getCriteriaBuilder().createQuery(aggregateClass);

        criteria.from(aggregateClass);

        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }
}
