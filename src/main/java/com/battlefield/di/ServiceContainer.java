package com.battlefield.di;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Простой IoC-контейнер (Dependency Injection).
 * Singleton — единственный экземпляр на приложение.
 * Все зависимости регистрируются по интерфейсу и разрешаются через resolve().
 */
public class ServiceContainer {

    private static final ServiceContainer INSTANCE = new ServiceContainer();
    private final Map<Class<?>, Object> beans = new ConcurrentHashMap<>();

    private ServiceContainer() {}

    public static ServiceContainer getInstance() {
        return INSTANCE;
    }

    public <T> void register(Class<T> type, T impl) {
        beans.put(type, impl);
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> type) {
        Object bean = beans.get(type);
        if (bean == null) {
            throw new IllegalStateException("Компонент не зарегистрирован: " + type.getName());
        }
        return (T) bean;
    }

    public void clear() {
        beans.clear();
    }
}
