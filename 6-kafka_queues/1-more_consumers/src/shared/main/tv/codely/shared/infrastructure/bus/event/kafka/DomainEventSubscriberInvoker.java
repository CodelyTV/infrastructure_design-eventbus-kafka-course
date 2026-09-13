package tv.codely.shared.infrastructure.bus.event.kafka;

import org.springframework.context.ApplicationContext;
import tv.codely.shared.domain.Service;
import tv.codely.shared.domain.bus.event.DomainEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Service
public final class DomainEventSubscriberInvoker {
    private final ApplicationContext context;

    public DomainEventSubscriberInvoker(ApplicationContext context) {
        this.context = context;
    }

    public boolean hasSubscriber(Class<?> subscriberClass) {
        return context.getBeanNamesForType(subscriberClass).length > 0;
    }

    public void invoke(Class<?> subscriberClass, DomainEvent event) throws InvocationTargetException, IllegalAccessException {
        handlerOf(subscriberClass).invoke(context.getBean(subscriberClass), event);
    }

    private static Method handlerOf(Class<?> subscriberClass) {
        return Arrays.stream(subscriberClass.getMethods())
                     .filter(method -> method.getName().equals("on"))
                     .filter(method -> method.getParameterCount() == 1)
                     .filter(method -> DomainEvent.class.isAssignableFrom(method.getParameterTypes()[0]))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                         String.format("The subscriber <%s> has no on(DomainEvent) method", subscriberClass.getName())
                     ));
    }
}
