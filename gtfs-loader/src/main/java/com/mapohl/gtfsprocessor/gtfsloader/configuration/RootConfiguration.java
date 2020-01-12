package com.mapohl.gtfsprocessor.gtfsloader.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.mapohl.gtfsprocessor.gtfsloader"})
public class RootConfiguration {
}
