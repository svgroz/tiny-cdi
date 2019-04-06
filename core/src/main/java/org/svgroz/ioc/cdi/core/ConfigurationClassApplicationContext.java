package org.svgroz.ioc.cdi.core;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class ConfigurationClassApplicationContext implements ApplicationContext {
    private static final Object[] EMPTY_ARRAY = new Object[]{};
    private static final Object NULL_OBJECT = new Object();

    final DirectedAcyclicGraph<Class<?>, DefaultEdge> classesGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
    final DirectedAcyclicGraph<Class<?>, DefaultEdge> dependencyGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
    final Map<Class<?>, Object> context = new HashMap<>();

    public ConfigurationClassApplicationContext(Collection<ServiceDefinition> configurations) {
        classesGraph.addVertex(Object.class);

        for (ServiceDefinition configuration : configurations) {
            Class sourceClass = configuration.getSourceClass();

            fillGraphByClass(classesGraph, sourceClass);
            dependencyGraph.addVertex(sourceClass);
            context.put(sourceClass, NULL_OBJECT);
        }

        fillDependenciesGraph(classesGraph, dependencyGraph);

    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return null;
    }

    DirectedAcyclicGraph<Class<?>, DefaultEdge> getContextGraph() {
        return dependencyGraph;
    }

    private void fillGraphByClass(DirectedAcyclicGraph<Class<?>, DefaultEdge> contextGraph, Class<?> clazz) {
        if (clazz == null || Objects.equals(clazz, Object.class)) {
            return;
        }

        contextGraph.addVertex(clazz);

        Class<?>[] interfaces = clazz.getInterfaces();

        for (Class<?> anInterface : interfaces) {
            contextGraph.addVertex(anInterface);
            contextGraph.addEdge(anInterface, clazz);

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
            DirectedAcyclicGraph<Class<?>, DefaultEdge> dependencyGraph
    ) {

        for (Class<?> dependency : dependencyGraph.vertexSet()) {
            Constructor<?> constructor = getDefaultConstructor(dependency);

            Parameter[] parameters = constructor.getParameters();
            if (parameters.length == 0) {
                continue;
            }

            for (Parameter parameter : parameters) {
                Class<?> parameterType = parameter.getType();

                if (!classesGraph.containsVertex(parameterType)) {
                    throw new IllegalArgumentException(dependency + " " + constructor + " " + parameter + " dependency not found");
                }

                List<Class<?>> injectCandidates = new ArrayList<>();
                DepthFirstIterator<Class<?>, DefaultEdge> candidatesIterator =
                        new DepthFirstIterator<>(classesGraph, parameterType);

                while (candidatesIterator.hasNext()) {
                    Class<?> next = candidatesIterator.next();
                    int modifiers = next.getModifiers();
                    if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
                        continue;
                    }

                    injectCandidates.add(next);
                }

                if (injectCandidates.isEmpty()) {
                    throw new IllegalArgumentException(dependency + " " + constructor + " " + parameter + " dependency not found");
                } else if (injectCandidates.size() > 1) {
                    throw new IllegalArgumentException(dependency + " " + constructor + " " + parameter + " find more than one inject candidate = " + injectCandidates.toString());
                }

                Class<?> aClass = injectCandidates.get(0);
                dependencyGraph.addEdge(aClass, dependency);
            }
        }
    }


    private void fillContext(
            DirectedAcyclicGraph<Class<?>, DefaultEdge> dependencyGraph,
            Map<Class<?>, Object> context
    ) {
        for (Class<?> aClass : dependencyGraph) {
            for (Class<?> parameter : dependencyGraph.getAncestors(aClass)) {
                Object o = context.get(parameter);
            }
        }
    }

    private Constructor<?> getDefaultConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            throw new IllegalArgumentException(clazz + " has no constructors");
        } else if (constructors.length > 1) {
            throw new IllegalArgumentException(clazz + " has more than one constructor");
        }

        return constructors[0];
    }

    private Object constructObject(Constructor<?> constructor, Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
