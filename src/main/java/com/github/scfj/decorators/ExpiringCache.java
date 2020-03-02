package com.github.scfj.decorators;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExpiringCache implements InvocationHandler {
    private final Object target;
    private final int expiringDelay;

    Map<String, Map<Integer, CacheEntry>> cache = new HashMap<>();

    private ExpiringCache(Object target, int timeToExpire) {
        this.target = target;
        this.expiringDelay = timeToExpire;
    }

    @SuppressWarnings("unchecked")
    public static <T> T decorate(T target, int msBeforeExpiring) {
        Class<?> targetClass = target.getClass();
        return (T) Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                targetClass.getInterfaces(),
                new ExpiringCache(target, msBeforeExpiring)
        );
    }

    @Override
    public Object invoke(Object _proxy, Method m, Object[] args) throws Throwable {
        CacheEntry cacheEntry = cachedResult(m, args);
        if (cacheEntry.expired()) {
            Object result = m.invoke(target, args);
            cacheEntry = new CacheEntry(result, expiringDelay);
            put(m, args, cacheEntry);
        }
        return cacheEntry.value;
    }

    private CacheEntry cachedResult(Method m, Object[] args) {
        Map<Integer, CacheEntry> methodCache = methodCache(m);
        if (!methodCache.containsKey(Objects.hash(args))) {
            return CacheEntry.empty;
        }
        return methodCache.get(Objects.hash(args));
    }

    private Map<Integer, CacheEntry> methodCache(Method m) {
        if (!cache.containsKey(m.getName())) {
            cache.put(m.getName(), new HashMap<Integer, CacheEntry>());
        }
        return cache.get(m.getName());
    }

    private void put(Method m, Object[] args, CacheEntry cacheEntry) {
        methodCache(m).put(Objects.hash(args), cacheEntry);
    }

    private static class CacheEntry {
        final Object value;
        final long expiresAt;

        CacheEntry(Object value, int expiresInMs) {
            this.value = value;
            this.expiresAt = System.currentTimeMillis() + expiresInMs;
        }

        boolean expired() {
            return expiresAt <= System.currentTimeMillis();
        }

        final static CacheEntry empty = new CacheEntry(new Object(), 0) {
            @Override
            boolean expired() {
                return true;
            }
        };
    }
}
