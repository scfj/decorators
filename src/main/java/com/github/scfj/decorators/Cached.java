package com.github.scfj.decorators;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cached implements InvocationHandler {
    private final Object target;
    private Map<String, Map<Integer, Object>> cache = new HashMap<>();

    private Cached(Object target) {
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    public static <T> T decorate(T target) {
        Class<?> targetClass = target.getClass();
        return (T) Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                targetClass.getInterfaces(),
                new Cached(target)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (present(method, args)) {
            return resultFromCache(method, args);
        } else {
            Object result = method.invoke(target, args);
            cache(method, args, result);
            return result;
        }
    }

    private boolean present(Method method, Object[] args) {
        return methodCache(method).containsKey(Objects.hash(args));
    }

    private Object resultFromCache(Method m, Object[] args) {
        return methodCache(m).get(Objects.hash(args));
    }

    private void cache(Method m, Object[] args, Object result) {
        methodCache(m).put(Objects.hash(args), result);
    }

    private Map<Integer, Object> methodCache(Method method) {
        if (!cache.containsKey(method.getName())) {
            cache.put(method.getName(), new HashMap<>());
        }
        return cache.get(method.getName());
    }
}
