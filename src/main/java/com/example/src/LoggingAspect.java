package com.example.src;

import com.example.annotation.aop.Aspect;
import com.example.annotation.aop.Before;

@Aspect
public class LoggingAspect {
    @Before("com.example.UserServiceInterface.processData")
    public static void logBeforeProcessing() {
        System.out.println("Before UserService.processData() is called.");
    }
}