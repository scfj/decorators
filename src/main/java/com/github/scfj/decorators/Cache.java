package com.github.scfj.decorators;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.WeakHashMap;

public class Cache implements InvocationHandler {
    private final Object target;
    private final WeakHashMap<Integer, Object> cache = new WeakHashMap<>();

    private Cache(Object target) {
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    public static <T> T decorate(T target) {
        Class<?> targetClass = target.getClass();
        return (T) Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                targetClass.getInterfaces(),
                new Cache(target)
        );
    }

    @Override
    public Object invoke(Object _proxy, Method method, Object[] args) throws Throwable {
        if (present(method, args)) {
            return resultFromCache(method, args);
        } else {
            Object result = method.invoke(target, args);
            cache(method, args, result);
            return result;
        }
    }

    private boolean present(Method method, Object[] args) {
        return cache.containsKey(hash(method, args));
    }

    private Object resultFromCache(Method m, Object[] args) {
        return cache.get(hash(m, args));
    }

    private void cache(Method m, Object[] args, Object result) {
        cache.put(hash(m, args), result);
    }

    private Integer hash(Method method, Object[] args) {
        return method.hashCode() ^ Objects.hash(args);
    }
}
