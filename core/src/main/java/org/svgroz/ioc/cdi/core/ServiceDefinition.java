package org.svgroz.ioc.cdi.core;

import java.util.Objects;

public class ServiceDefinition {
    private final Class sourceClass;

    public ServiceDefinition(final Class sourceClass) {
        this.sourceClass = sourceClass;
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceDefinition that = (ServiceDefinition) o;
        return Objects.equals(sourceClass, that.sourceClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceClass);
    }

    @Override
    public String toString() {
        return "ServiceDefinition{" +
                "sourceClass=" + sourceClass +
                '}';
    }
}
