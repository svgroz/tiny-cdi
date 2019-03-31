package org.svgroz.ioc.cdi.core;

import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.GraphExporter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigurationTest {
    @Test
    @SuppressWarnings("unchecked")
    public void test1() throws Exception {
        ConfigurationClassApplicationContext ctx = new ConfigurationClassApplicationContext(Arrays.asList(
                new ServiceDefinition(Bar.class),
                new ServiceDefinition(Foo.class),
                new ServiceDefinition(Biz.class)));

        GraphExporter exporter = new DOTExporter(
                x -> Integer.toString(x.hashCode()),
                Object::toString,
                null);
        exporter.exportGraph(ctx.getContextGraph(), System.out);
    }


    public interface IntegerFunction extends Function<Integer, Integer> {

    }

    public static class Foo implements IntegerFunction {
        @Override
        public Integer apply(Integer integer) {
            return 42;
        }
    }

    public static class Bar implements Consumer<Integer> {
        public Bar(Function<Integer, Integer> mapFunction) {

        }

        @Override
        public void accept(Integer aInteger) {

        }
    }

    public static class Biz implements Supplier<Integer> {
        public Biz(Function foo, Bar bar) {
        }

        @Override
        public Integer get() {
            return 42;
        }
    }
}
