package tv.codely.shared.infrastructure.bus.event;

import org.reflections.Reflections;
import tv.codely.shared.domain.Service;
import tv.codely.shared.domain.bus.event.DomainEvent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public final class DomainEventsInformation {
    private static final String WILDCARD = "*";

    HashMap<String, Class<? extends DomainEvent>> indexedDomainEvents;

    public DomainEventsInformation() {
        Reflections                       reflections = new Reflections("tv.codely");
        Set<Class<? extends DomainEvent>> classes     = reflections.getSubTypesOf(DomainEvent.class);

        try {
            indexedDomainEvents = formatEvents(classes);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Class<? extends DomainEvent> forName(String name) {
        return indexedDomainEvents.get(name);
    }

    public List<String> eventNames() {
        return indexedDomainEvents.keySet().stream().filter(name -> !isPattern(name)).sorted().toList();
    }

    public List<String> eventNamesMatching(String nameOrPattern) {
        Pattern pattern = Pattern.compile(nameOrPattern.replace(".", "\\.").replace(WILDCARD, ".*"));

        return eventNames().stream().filter(name -> pattern.matcher(name).matches()).toList();
    }

    public String forClass(Class<? extends DomainEvent> domainEventClass) {
        return indexedDomainEvents.entrySet()
                                  .stream()
                                  .filter(entry -> Objects.equals(entry.getValue(), domainEventClass))
                                  .map(Map.Entry::getKey)
                                  .findFirst().orElse("");
    }

    private boolean isPattern(String name) {
        return name.contains(WILDCARD);
    }

    private HashMap<String, Class<? extends DomainEvent>> formatEvents(
        Set<Class<? extends DomainEvent>> domainEvents
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        HashMap<String, Class<? extends DomainEvent>> events = new HashMap<>();

        for (Class<? extends DomainEvent> domainEvent : domainEvents) {
            if (Modifier.isAbstract(domainEvent.getModifiers()) && !domainEvent.isInterface()) {
                continue;
            }

            events.put(instanceOf(domainEvent).eventName(), domainEvent);
        }

        return events;
    }

    private DomainEvent instanceOf(Class<? extends DomainEvent> domainEvent)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (domainEvent.isInterface()) {
            return (DomainEvent) Proxy.newProxyInstance(
                domainEvent.getClassLoader(),
                new Class<?>[]{domainEvent},
                (proxy, method, args) -> InvocationHandler.invokeDefault(proxy, method, args)
            );
        }

        return domainEvent.getConstructor().newInstance();
    }
}
