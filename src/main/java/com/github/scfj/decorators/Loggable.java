package com.github.scfj.decorators;

import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Loggable implements InvocationHandler {
    private final Object target;
    private final PrintStream output;

    private Loggable(Object target, PrintStream output) {
        this.target = target;
        this.output = output;
    }

    @SuppressWarnings("unchecked")
    public static <T> T decorate(T target, PrintStream output) {
        Class<?> targetClass = target.getClass();
        return (T) Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                targetClass.getInterfaces(),
                new Loggable(target, output)
        );
    }

    public static <T> T decorate(T target) {
        return decorate(target, System.out);
    }

    @Override
    public Object invoke(Object _proxy, Method method, Object[] args) throws Throwable {
        try {
            Object result = method.invoke(target, args);
            log(method, args, result);
            return result;
        } catch (InvocationTargetException invocationException) {
            Throwable targetException = invocationException.getTargetException();
            log(method, args, targetException);
            throw targetException;
        }
    }

    private void log(Method method, Object[] args, Object result) {
        String message = String.format(
                "LOG.%s: %s.%s(%s) = %s",
                target.getClass().getName(),
                target,
                method.getName(),
                formatArgs(args),
                result
        );
        output.println(message);
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Object arg : args) {
            stringBuilder.append(arg.toString());
            stringBuilder.append(", ");
        }
        stringBuilder.setLength(stringBuilder.length() - 2);
        return stringBuilder.toString();
    }
}
