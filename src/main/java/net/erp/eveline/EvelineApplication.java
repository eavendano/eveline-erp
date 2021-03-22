package net.erp.eveline;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import net.erp.eveline.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableAdminServer
@EnableRetry
@SpringBootApplication
@Import({SecurityConfig.class})
public class EvelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvelineApplication.class, args);
    }

}
