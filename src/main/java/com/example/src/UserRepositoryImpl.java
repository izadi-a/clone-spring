package com.example.src;

import com.example.annotation.Component;
import com.example.annotation.PostConstruct;
import com.example.annotation.PreDestroy;

@Component
public class UserRepositoryImpl implements UserRepository {

    @PostConstruct
    public void initialize() {
        System.out.println("UserRepository initialized.");
    }

    public String getData(String id) {
        return "Data from UserRepository" + id;
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("UserRepository is being destroyed.");
    }
}