package com.example.src;

import com.example.annotation.Component;
import com.example.annotation.PreDestroy;

@Component
public class LoggerService {
    public void log(String message) {
        System.out.println("LOG: " + message);
    }

    @PreDestroy
    public void shutdownLogger() {
        System.out.println("LoggerService shutting down.");
    }
}