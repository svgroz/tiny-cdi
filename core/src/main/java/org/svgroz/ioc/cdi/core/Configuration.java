package org.svgroz.ioc.cdi.core;

import java.util.Collection;

@FunctionalInterface
public interface Configuration {
    Collection<ServiceDefinition> configurationPart();
}
