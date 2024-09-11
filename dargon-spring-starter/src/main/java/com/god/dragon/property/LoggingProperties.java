package com.god.dragon.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @implNote start with 2024/9/11 13:25
 */
@Component
@ConfigurationProperties(prefix = "spring.web.logging")
@Getter
@Setter
public class LoggingProperties {
    private boolean enabled;
    //spring:
    //  web:
    //    logging:
    //      include-paths:
    //      -  /xxx/**
    private List<String> includePaths;
}
