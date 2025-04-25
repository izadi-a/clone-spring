package com.example.container;

class BeanDefinition {
    private final Class<?> beanClass;
    private final String scope;
    private Object singletonInstance; // For singleton scope

    public BeanDefinition(Class<?> beanClass, String scope) {
        this.beanClass = beanClass;
        this.scope = scope;
        this.singletonInstance = null;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getScope() {
        return scope;
    }

    public Object getSingletonInstance() {
        return singletonInstance;
    }

    public void setSingletonInstance(Object singletonInstance) {
        this.singletonInstance = singletonInstance;
    }
}