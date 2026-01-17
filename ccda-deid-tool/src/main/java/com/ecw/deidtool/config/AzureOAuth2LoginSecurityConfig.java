package com.ecw.deidtool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
@Profile("azure")
public class AzureOAuth2LoginSecurityConfig {

    /**
     * Add configuration logic as needed.
     */
    @Bean
    public SecurityFilterChain htmlFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/textmode/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/**").permitAll()
//                        .requestMatchers("/files/**").authenticated()
                        .requestMatchers("/files/**").permitAll()
                        .requestMatchers("/textmode").permitAll()
//                        .requestMatchers("/**").permitAll()
//                        .requestMatchers("/").permitAll()
//                        .requestMatchers("/fileUpload/**").authenticated()
                        .anyRequest().authenticated())
//                        .anyRequest().permitAll())
                .oauth2Login(Customizer.withDefaults())
                .oauth2Client(Customizer.withDefaults())
                        .csrf(csrf -> csrf.disable());
        // @formatter:on
        return http.build();
    }
}
