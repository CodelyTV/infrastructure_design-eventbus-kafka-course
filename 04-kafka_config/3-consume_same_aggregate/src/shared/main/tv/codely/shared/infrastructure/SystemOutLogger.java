package tv.codely.shared.infrastructure;

import tv.codely.shared.domain.Logger;
import tv.codely.shared.domain.Service;

import java.io.Serializable;
import java.util.HashMap;

@Service
public final class SystemOutLogger implements Logger {
    @Override
    public void info(String message) {
        print("INFO", message, new HashMap<>());
    }

    @Override
    public void info(String message, HashMap<String, Serializable> context) {
        print("INFO", message, context);
    }

    @Override
    public void warning(String message) {
        print("WARNING", message, new HashMap<>());
    }

    @Override
    public void warning(String message, HashMap<String, Serializable> context) {
        print("WARNING", message, context);
    }

    @Override
    public void critical(String message) {
        print("CRITICAL", message, new HashMap<>());
    }

    @Override
    public void critical(String message, HashMap<String, Serializable> context) {
        print("CRITICAL", message, context);
    }

    private void print(String level, String message, HashMap<String, Serializable> context) {
        System.out.println(String.format("[%s] %s %s", level, message, context));
    }
}
