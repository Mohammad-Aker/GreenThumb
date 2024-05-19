package com.GreenThumb.GT.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {
    private String uploadDir;

}