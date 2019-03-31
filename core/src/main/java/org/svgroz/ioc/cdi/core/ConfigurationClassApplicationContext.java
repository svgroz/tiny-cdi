package org.svgroz.ioc.cdi.core;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.svgroz.ioc.cdi.core.util.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;

public class ConfigurationClassApplicationContext implements ApplicationContext {
    private static final Object[] EMPTY_ARRAY = new Object[]{};
    private static final Object NULL_OBJECT = new Object();

    private final DirectedAcyclicGraph<Class<?>, DefaultEdge> classesGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
    private final DirectedAcyclicGraph<Class<?>, DefaultEdge> dependenciesGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);

    public ConfigurationClassApplicationContext(Collection<ServiceDefinition> configurations) {
        classesGraph.addVertex(Object.class);
        Map<Class<?>, Object> instancesCtx = new HashMap<>();

        for (ServiceDefinition configuration : configurations) {
            Class sourceClass = configuration.getSourceClass();

            fillGraphByClass(classesGraph, sourceClass);
            dependenciesGraph.addVertex(sourceClass);
            instancesCtx.put(sourceClass, NULL_OBJECT);
        }

        fillDependenciesGraph(classesGraph, dependenciesGraph);

        TopologicalOrderIterator<Class<?>, DefaultEdge> graphIterator = new TopologicalOrderIterator<>(classesGraph);
        while (graphIterator.hasNext()) {
            Class<?> next = graphIterator.next();
            if (!instancesCtx.containsKey(next)) {
                continue;
            }

            System.out.println(next);
        }
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return null;
    }

    DirectedAcyclicGraph<Class<?>, DefaultEdge> getContextGraph() {
        return null;
    }

    private Object constructObject(Constructor<?> constructor, Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void fillGraphByClass(DirectedAcyclicGraph<Class<?>, DefaultEdge> contextGraph, Class<?> clazz) {
        if (clazz == null || Objects.equals(clazz, Object.class)) {
            return;
        }

        contextGraph.addVertex(clazz);

        Class<?>[] interfaces = clazz.getInterfaces();

        for (Class<?> anInterface : interfaces) {
            contextGraph.addVertex(anInterface);
            contextGraph.addEdge(clazz, anInterface);

            fillGraphByClass(contextGraph, anInterface);
        }

        Class<?> superclass = clazz.getSuperclass();

        if (superclass == null) {
            return;
        }

        contextGraph.addEdge(superclass, clazz);
        fillGraphByClass(contextGraph, superclass);
    }

    private void fillDependenciesGraph(
            DirectedAcyclicGraph<Class<?>, DefaultEdge> classesGraph,
            DirectedAcyclicGraph<Class<?>, DefaultEdge> dependenciesGraph) {

        List<Pair<Class<?>, Class<?>>> edges = new ArrayList<>();
        Set<Class<?>> services = dependenciesGraph.vertexSet();

        for (Class<?> clazz : services) {
            Constructor<?>[] constructors = clazz.getConstructors();
            if (constructors.length != 1) {
                throw new IllegalArgumentException("Find more than 1 constructor in " + clazz.getName());
            }

            Constructor<?> constructor = constructors[0];
            for (Parameter parameter : constructor.getParameters()) {
                Class<?> parameterType = parameter.getType();
                edges.add(Pair.of(clazz, parameterType));
            }
        }

        for (Pair<Class<?>, Class<?>> edge : edges) {
            Class<?> source = edge.getLeft();
            Class<?> dependency = edge.getRight();

            Set<DefaultEdge> dependencyEdges = classesGraph.edgesOf(dependency);
            for (DefaultEdge dependencyEdge : dependencyEdges) {
                System.out.println(classesGraph.getEdgeTarget(dependencyEdge));
            }
        }
    }
}
