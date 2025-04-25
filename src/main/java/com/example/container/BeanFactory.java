package com.example.container;

import com.example.annotation.Autowired;
import com.example.annotation.Component;
import com.example.annotation.PostConstruct;
import com.example.annotation.PreDestroy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

public class BeanFactory {
    private Map<Class<?>, Object> beans = new HashMap<>();

    public BeanFactory(String basePackage) {
        try {
            scanComponents(basePackage);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception appropriately
        }
    }

    // Method to register a bean (initially, we might do this manually)
    public void registerBean(Class<?> beanClass, Object instance) {
        beans.put(beanClass, instance);
    }

    private void scanComponents(String basePackage) throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String path = basePackage.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new LinkedList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        for (File directory : dirs) {
            findClasses(directory, basePackage);
        }
        // After all beans are instantiated, inject their dependencies
        for (Object bean : beans.values()) {
            injectFields(bean);
        }
        // After all beans are instantiated and dependencies injected, invoke @PostConstruct
        for (Object bean : beans.values()) {
            injectFields(bean); // Inject fields first
            invokePostConstruct(bean);
        }
    }

    private void findClasses(File directory, String packageName) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClasses(file, packageName + "." + file.getName());
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName((className));
                    if (clazz.isAnnotationPresent(Component.class)) {
                        Object instance = createInstance(clazz);
                        if (instance != null) {
                            beans.put(clazz, instance);
                            System.out.println("Registered bean: " + clazz.getName());
                        }
                    }
                }
            }
        }
    }

    private Object createInstance(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> autowiredConstructor = null;

        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                if (autowiredConstructor != null) {
                    throw new IllegalStateException("Found multiple @Autowired constructors in " + clazz.getName());
                }
                autowiredConstructor = constructor;
            }
        }

        if (autowiredConstructor != null) {
            Parameter[] parameters = autowiredConstructor.getParameters();
            Object[] dependencies = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Class<?> parameterType = parameters[i].getType();
                Object dependency = getBean(parameterType);
                if (dependency == null) {
                    throw new RuntimeException("Could not resolve dependency of type " + parameterType.getName() + " for constructor in " + clazz.getName());
                }
                dependencies[i] = dependency;
            }
            return autowiredConstructor.newInstance(dependencies);
        } else {
            // Fallback to no-argument constructor
            return clazz.getDeclaredConstructor().newInstance();
        }
    }

    private void injectFields(Object bean) throws IllegalAccessException {
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> fieldType = field.getType();
                Object dependency = getBean(fieldType);
                if (dependency != null) {
                    field.setAccessible(true);
                    try {
                        field.set(bean, dependency);
                        System.out.println("Injected " + fieldType.getName() + " into " + clazz.getName() + "." + field.getName());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error injecting dependency of type " + fieldType.getName() + " into " + clazz.getName() + "." + field.getName() + ": Incorrect argument type.");
                    }
                } else {
                    throw new RuntimeException("Could not find dependency of type " + fieldType.getName() + " for field " + clazz.getName() + "." + field.getName());
                }
            }
        }
    }

    private void invokePostConstruct(Object bean) {
        Class<?> clazz = bean.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                try {
                    method.setAccessible(true);
                    method.invoke(bean);
                    System.out.println("Invoked @PostConstruct method: " + clazz.getName() + "." + method.getName());
                } catch (InvocationTargetException | IllegalAccessException e) {
                    System.err.println("Error invoking @PostConstruct method " + clazz.getName() + "." + method.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    public void shutdown() {
        System.out.println("Shutting down BeanContainer...");
        // Iterate through beans and invoke @PreDestroy methods
        for (Object bean : beans.values()) {
            Class<?> clazz = bean.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(PreDestroy.class)) {
                    try {
                        method.setAccessible(true);
                        method.invoke(bean);
                        System.out.println("Invoked @PreDestroy method: " + clazz.getName() + "." + method.getName());
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        System.err.println("Error invoking @PreDestroy method " + clazz.getName() + "." + method.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
        beans.clear(); // Clean up the bean map
        System.out.println("BeanContainer shut down.");
    }

    // Method to retrieve a bean
    public <T> T getBean(Class<T> beanType) {
        return (T) beans.get(beanType);
    }
}