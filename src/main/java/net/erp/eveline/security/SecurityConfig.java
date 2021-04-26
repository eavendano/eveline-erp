package net.erp.eveline.security;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.UUID;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final AdminServerProperties adminServer;

    public SecurityConfig(AdminServerProperties adminServer) {
        this.adminServer = adminServer;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(this.adminServer.path("/"));

        http
                .authorizeRequests(
                        (authorizeRequests) ->
                                authorizeRequests
                                        .antMatchers("/docs/**").permitAll() // This is how a path can be white listed within the application
                                        .antMatchers("/provider/**").permitAll()
                                        .antMatchers("/product/**").permitAll()
                                        .antMatchers(this.adminServer.path("/assets/**")).permitAll()
                                        .antMatchers(this.adminServer.path("/login")).permitAll()
                                        .anyRequest().authenticated()
                ).formLogin((formLogin) -> formLogin.loginPage(this.adminServer.path("/login")).successHandler(successHandler).and())
                .logout((logout) -> logout.logoutUrl(this.adminServer.path("/logout")))
                .httpBasic(Customizer.withDefaults())
                .csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher(this.adminServer.path("/instances"),
                                        POST.toString()),
                                new AntPathRequestMatcher(this.adminServer.path("/instances/*"),
                                        DELETE.toString()),
                                new AntPathRequestMatcher(this.adminServer.path("/actuator/**")),
                                new AntPathRequestMatcher("/provider/**", PUT.toString()),
                                new AntPathRequestMatcher("/product/**", PUT.toString()),
                                new AntPathRequestMatcher("/warehouse/**", PUT.toString()),
                                new AntPathRequestMatcher("/brand/**", PUT.toString())
                        ))
                .rememberMe((rememberMe) -> rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600))
                .headers()
                .xssProtection()
                .and()
                .contentSecurityPolicy("script-src 'self'");
    }

    // Required to provide UserDetailsService for "remember functionality"
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("{noop}26AROUNDyearSOFT4411").roles("USER");
    }
}
