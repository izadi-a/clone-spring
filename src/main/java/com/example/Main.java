package com.example;

import com.example.container.BeanFactory;
import com.example.src.UserService;

public class Main {
    public static void main(String[] args) {
        BeanFactory container = new BeanFactory("com.example");
        UserService userService = null;
        try {
            userService = container.getBean(UserService.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (userService != null) {
            userService.processData(); // Output: UserService processing: Data from UserRepository
        } else {
            System.out.println("UserService not found in the container.");
        }
        // Simulate application shutdown
        container.shutdown();
    }
}