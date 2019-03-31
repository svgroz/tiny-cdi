package org.svgroz.ioc.cdi.core;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.junit.jupiter.api.Test;

public class GraphExamples {
    @Test
    public void test1() {
        DirectedAcyclicGraph<String, DefaultEdge> contextGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        contextGraph.addVertex("a");
        contextGraph.addVertex("b");
        contextGraph.addVertex("c");

        contextGraph.addEdge("a", "b");
        contextGraph.addEdge("b", "c");
        contextGraph.addEdge("c", "a");

        TopologicalOrderIterator<String, DefaultEdge> iterator = new TopologicalOrderIterator<>(contextGraph);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
