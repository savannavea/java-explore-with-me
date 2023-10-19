package ru.practicum.event.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.event.dto.Criteria;
import ru.practicum.event.dto.CriteriaPub;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.event.enums.EventState.PUBLISHED;

@Repository
@AllArgsConstructor
public class CustomEventRepository {

    private final EntityManager entityManager;

    public List<Event> getEvents(Criteria criteria) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = builder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getUsers() != null) {
            Predicate user = root.get("initiator").in(criteria.getUsers());
            predicates.add(user);
        }

        if (criteria.getStates() != null) {
            Predicate state = root.get("state").in(criteria.getStates());
            predicates.add(state);
        }

        if (criteria.getCategories() != null) {
            Predicate categories = root.get("category").in(criteria.getCategories());
            predicates.add(categories);
        }

        if (criteria.getStart() != null) {
            Predicate start = builder.greaterThan(root.get("eventDate"), criteria.getStart());
            predicates.add(start);
        }

        if (criteria.getEnd() != null) {
            Predicate end = builder.lessThan(root.get("eventDate"), criteria.getEnd());
            predicates.add(end);
        }

        CriteriaQuery<Event> select = criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        TypedQuery<Event> typeQuery = entityManager.createQuery(select);
        typeQuery.setFirstResult(criteria.getFrom());
        typeQuery.setMaxResults(criteria.getSize());

        return typeQuery.getResultList();
    }

    public List<Event> getEventsPublic(CriteriaPub criteriaPub) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = builder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        Predicate publicationStatus = builder.equal(root.get("state"), PUBLISHED);
        predicates.add(publicationStatus);

        if (criteriaPub.getText() != null) {
            String searchText = "%" + criteriaPub.getText().toLowerCase() + "%";

            Predicate searchInAnnotation = builder.like(builder.lower(root.get("annotation")), searchText);
            Predicate searchInDescription = builder.like(builder.lower(root.get("description")), searchText);

            Predicate test = builder.or(searchInAnnotation, searchInDescription);
            predicates.add(test);
        }

        if (criteriaPub.getCategories() != null) {
            Predicate categories = root.get("category").in(criteriaPub.getCategories());
            predicates.add(categories);
        }

        if (criteriaPub.getPaid() != null) {
            Predicate paid = builder.equal(root.get("paid"), criteriaPub.getPaid());
            predicates.add(paid);
        }

        if (criteriaPub.getRangeStart() != null) {
            Predicate start = builder.greaterThan(root.get("eventDate"), criteriaPub.getRangeStart());
            predicates.add(start);
        }

        if (criteriaPub.getRangeEnd() != null) {
            Predicate end = builder.lessThan(root.get("eventDate"), criteriaPub.getRangeEnd());
            predicates.add(end);
        }

        if (criteriaPub.getOnlyAvailable()) {
            Subquery<Long> sub = criteriaQuery.subquery(Long.class);
            Root<Request> subRoot = sub.from(Request.class);
            Join<Request, Event> subParticipation = subRoot.join("event");
            sub.select(builder.count(subRoot.get("event")));
            sub.where(builder.equal(root.get("id"), subParticipation.get("id")));
            sub.where(builder.equal(subRoot.get("status"), EventState.CONFIRMED));
            Predicate onlyAvailable = builder.greaterThan(root.get("participantLimit"), sub);

            predicates.add(onlyAvailable);
        }

        CriteriaQuery<Event> select = criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        TypedQuery<Event> typeQuery = entityManager.createQuery(select);
        typeQuery.setFirstResult(criteriaPub.getFrom());
        typeQuery.setMaxResults(criteriaPub.getSize());

        return typeQuery.getResultList();
    }
}