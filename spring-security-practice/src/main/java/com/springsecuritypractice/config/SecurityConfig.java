package com.springsecuritypractice.config;

import com.springsecuritypractice.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableGlobalAuthentication
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Autowired
    public SecurityConfig(PrincipalOauth2UserService principalOauth2UserService) {
        this.principalOauth2UserService = principalOauth2UserService;
    }

    @Bean
    public BCryptPasswordEncoder encoderPwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/user/**").authenticated()
                .requestMatchers("/manager/**").hasAnyAuthority("ADMIN", "MANAGER")
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().permitAll()
                .and()
                .formLogin(form -> form
                        .loginPage("/loginForm")
                        .loginProcessingUrl("/login")   // /login이 호출되면 시큐리티가 낚아채어 대신 로그인 진행해줌
                        .defaultSuccessUrl("/"))
                .logout().logoutSuccessUrl("/")
                .and()
                .oauth2Login(form -> form
                        .loginPage("/loginForm")    //구글 로그인이 완료되면 엑세스토큰과 사용자 프로필정보를 받음
                        .userInfoEndpoint()
                        .userService(principalOauth2UserService))
                .build();
    }
}