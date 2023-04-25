package be.odisee.ti2.ddf.timesheet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChai(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/timesheetrest/**").permitAll()
                        .requestMatchers("/timesheetrest/categoriesWithProjects").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/login*").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/admin", "/h2/**").hasRole("ADMIN")
                        .requestMatchers("/**").authenticated()
                )
                .formLogin(form -> form
                    .loginPage("/login")
                    .failureUrl("/login-error")
                    .defaultSuccessUrl("/timesheet",true)
                )
                .exceptionHandling().accessDeniedPage("/403.html");
        http.headers().frameOptions().disable();                // NEEDED FOR H2 CONSOLE
        return http.build();
    }

    /**
     * The default PasswordEncoder is now DelegatingPasswordEncoder which is a non-passive change.
     * This change ensures that passwords are now encoded using BCrypt by default
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
