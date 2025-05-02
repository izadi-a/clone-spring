package com.example.annotation.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class AspectInvocationHandler implements InvocationHandler {
    private final Object target;
    private final Map<Method, Method> beforeAdvice; // Target method -> Advice method

    public AspectInvocationHandler(Object target, Map<Method, Method> beforeAdvice) {
        this.target = target;
        this.beforeAdvice = beforeAdvice;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method adviceMethod = beforeAdvice.get(method);
        if (adviceMethod != null) {
            adviceMethod.invoke(null, args); // For simplicity, assuming static advice methods for now
        }
        return method.invoke(target, args);
    }
}