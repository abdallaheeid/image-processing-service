package org.abdallah.imageprocessingservice;

import org.abdallah.imageprocessingservice.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class ImageProcessingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageProcessingServiceApplication.class, args);
    }

}
