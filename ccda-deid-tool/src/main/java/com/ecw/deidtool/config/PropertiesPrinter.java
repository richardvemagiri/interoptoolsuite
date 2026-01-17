package com.ecw.deidtool.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class PropertiesPrinter {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @EventListener
    public void handleContextRefreshed(ContextRefreshedEvent contextRefreshedEvent){

        log.debug("Spring Profile: " + activeProfile);
        ConfigurableEnvironment env = (ConfigurableEnvironment) contextRefreshedEvent.getApplicationContext().getEnvironment();
        env.getPropertySources()
                .stream()
                .filter(ps -> ps instanceof MapPropertySource &&

                        ps.getName().contains("application-" + activeProfile + ".properties"))
                .map(ps -> ((MapPropertySource) ps).getSource().keySet())
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .forEach(key -> log.debug("{}={}", key, env.getProperty(key)));



    }
}
