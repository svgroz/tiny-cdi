package org.svgroz.ioc.cdi.core;

public interface ApplicationContext {
    <T> T getBean (Class<T> clazz);
}
