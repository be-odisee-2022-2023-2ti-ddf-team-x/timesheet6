package be.odisee.ti2.se4.timesheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = false,
        jsr250Enabled = false
)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/timesheetrest/**").permitAll()
                .antMatchers("/timesheetrest/categoriesWithProjects").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/login*").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/admin", "/h2/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .failureUrl("/login-error")
                    .defaultSuccessUrl("/timesheet",true)
                    .permitAll()
                .and()
                .logout()
                    .invalidateHttpSession(true)
                    .logoutSuccessUrl("/byebye")
                    .permitAll();
        http.exceptionHandling().accessDeniedPage("/403");
        http.csrf().disable();                                  // NEEDED FOR H2 CONSOLE
        http.headers().frameOptions().disable();                // NEEDED FOR H2 CONSOLE
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
