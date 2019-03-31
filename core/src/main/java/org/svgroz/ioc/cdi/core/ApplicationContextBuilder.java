package org.svgroz.ioc.cdi.core;

import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContextBuilder {
    private final List<Configuration> configurations = new ArrayList<>();

    public ApplicationContextBuilder addConfiguration(Configuration configuration) {
        this.configurations.add(configuration);
        return this;
    }

    public ApplicationContextBuilder addConfiguration(Collection<Configuration> configurations) {
        this.configurations.addAll(configurations);
        return this;
    }

    public ApplicationContextBuilder addConfiguration(Configuration... configurations) {
        if (configurations == null) {
            throw new IllegalArgumentException("Configurations should be not null");
        }
        if (configurations.length < 1) {
            throw new IllegalArgumentException("Configurations array is empty");
        }

        if (configurations.length == 1) {
            return addConfiguration(configurations[0]);
        } else {
            return addConfiguration(Arrays.asList(configurations));
        }
    }

    public ApplicationContext build() {
        List<ServiceDefinition> collect = configurations.stream()
                .flatMap(x -> x.configurationPart().stream())
                .collect(Collectors.toList());
        return new ConfigurationClassApplicationContext(collect);
    }
}
