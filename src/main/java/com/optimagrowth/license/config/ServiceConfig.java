package com.optimagrowth.license.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "example") //Все другие настройки конфигурации внедряются с помощью этой аннотации
// (Помимо той настройки которая внедряется при запуске приложения)
public class ServiceConfig {
    private String property;
}
