package org.svgroz.ioc.cdi.core;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Objects;

public class ConfigurationClassApplicationContext implements ApplicationContext {
    private static final Object[] EMPTY_ARRAY = new Object[]{};

    private final DirectedAcyclicGraph<Class<?>, DefaultEdge> contextGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);

    public ConfigurationClassApplicationContext(Collection<ServiceDefinition> configurations) {
        contextGraph.addVertex(Object.class);

        for (ServiceDefinition configuration : configurations) {
            fillGraphByClass(configuration.getSourceClass());
        }
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return null;
    }

    DirectedAcyclicGraph<Class<?>, DefaultEdge> getContextGraph() {
        return contextGraph;
    }

    private Object constructObject(Constructor<?> constructor, Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void fillGraphByClass(Class<?> clazz) {
        if (clazz == null || Objects.equals(clazz, Object.class)) {
            return;
        }

        contextGraph.addVertex(clazz);

        Class<?>[] interfaces = clazz.getInterfaces();

        for (Class<?> anInterface : interfaces) {
            contextGraph.addVertex(anInterface);
            contextGraph.addEdge(anInterface, clazz);

            fillGraphByClass(anInterface);
        }

        Class<?> superclass = clazz.getSuperclass();

        if (superclass != null) {
            contextGraph.addEdge(superclass, clazz);

            fillGraphByClass(superclass);
        }
    }
}
