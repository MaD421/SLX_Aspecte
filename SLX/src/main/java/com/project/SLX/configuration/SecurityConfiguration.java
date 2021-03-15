package com.project.SLX.configuration;

import com.project.SLX.repository.UserRepository;
import com.project.SLX.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public SecurityConfiguration(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(customUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable(); // Disable CSRF protection
        httpSecurity.headers().frameOptions().disable();
        httpSecurity.authorizeRequests()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/image/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/user/login").permitAll()
                .antMatchers("/user/register").permitAll()
                .antMatchers("/user/register/process").permitAll()
                .antMatchers("/viewListing/**").permitAll()
                .antMatchers("/user/{\\d+}/listings/**").permitAll()
                .antMatchers("/pg/**").permitAll()
                .antMatchers("/error").permitAll()
                //.antMatchers("/message/**").permitAll() // Temporary for testing
                .anyRequest().authenticated()
                .and().formLogin() // Login configuration
                .loginPage("/user/login")
                .loginProcessingUrl("/app-login")
                .defaultSuccessUrl("/")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
                .and().logout() // Logout configuration
                .logoutUrl("/user/logout");
    }
}
