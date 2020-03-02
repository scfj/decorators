package com.github.scfj.decorators;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Robust implements InvocationHandler {
    private final Object target;
    private int attemptsLeft;
    private final int delay;

    private Robust(Object target, int attempts, int delayInMs) {
        this.target = target;
        this.attemptsLeft = attempts;
        this.delay = delayInMs;
    }

    public static <T> T decorate(T target, int attempts) {
        return decorate(target, attempts, 0);
    }

    @SuppressWarnings("unchecked")
    public static <T> T decorate(T target, int attempts, int delayInMs) {
        Class<?> targetClass = target.getClass();
        return (T) Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                targetClass.getInterfaces(),
                new Robust(target, attempts, delayInMs)
        );
    }

    @Override
    public Object invoke(Object _proxy, Method method, Object[] args) throws Throwable {
        Throwable lastError = new RuntimeException();
        while (attemptsLeft > 0) {
            try {
                attemptsLeft -= 1;
                return method.invoke(target, args);
            } catch (InvocationTargetException invocationError) {
                lastError = invocationError.getTargetException();
            }
            sleep();
        }
        throw lastError;
    }

    private void sleep() {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
