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
                new ServiceDefinition(Baz.class),
                new ServiceDefinition(Buz.class),
                new ServiceDefinition(Biz.class)));

        GraphExporter exporter = new DOTExporter(
                x -> x.toString().replace(' ', '_').replace('.', '_').replace('$', '_'),
                Object::toString,
                null);
        exporter.exportGraph(ctx.getContextGraph(), System.out);
    }


    public interface IntegerFunction extends Function<Integer, Integer> {

    }

    public static class Foo implements IntegerFunction {
        @Override
        public Integer apply(Integer integer) {
            return integer + 1;
        }
    }

    public static class Bar implements Consumer<Integer> {
        private final Function<Integer, Integer> mapFunction;

        public Bar(Function<Integer, Integer> mapFunction) {
            this.mapFunction = mapFunction;
        }

        @Override
        public void accept(Integer aInteger) {
            this.mapFunction.apply(aInteger);
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

    public static class Baz implements Supplier<Integer> {
        public Baz(Bar bar) {
        }

        @Override
        public Integer get() {
            return 42;
        }
    }

    public static class Buz implements Function<Integer, Integer> {
        @Override
        public Integer apply(Integer integer) {
            return 42;
        }
    }
}
