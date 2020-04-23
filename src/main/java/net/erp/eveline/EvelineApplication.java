package net.erp.eveline;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAdminServer
@SpringBootApplication
@Import({SecurityConfig.class})
public class EvelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvelineApplication.class, args);
    }

}
