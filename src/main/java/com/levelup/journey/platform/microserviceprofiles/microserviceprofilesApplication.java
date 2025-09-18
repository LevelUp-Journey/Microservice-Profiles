package com.levelup.journey.platform.microserviceprofiles;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.Console;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class microserviceprofilesApplication implements CommandLineRunner {

    @Value("${server.port:8082}")
    private String serverPort;

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerPath;

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMissing()
                .load();

        // Set system properties from .env file
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        SpringApplication.run(microserviceprofilesApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String swaggerUrl = "http://localhost:" + serverPort + swaggerPath;

        Console console = System.console();
        if (console != null) {
            console.printf("Swagger UI is available at: %s%n", swaggerUrl);
        } else {
            System.out.println("Swagger UI is available at: " + swaggerUrl);
        }
    }
}
