package com.example.demo.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form
                        .successHandler((request, response, authentication) -> {
                                response.sendRedirect("http://localhost:3000/home");
                        }) // 登入成功後重定向到前端頁面,false表示不開新頁面
                        .permitAll()
                        .failureUrl("http://localhost:8080/"))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/api/createUser", "/api/getMeetingRoomList", "/api/leaseMeetingRoom",
                                "/api/updateMeetingRoom", "/api/deleteMeetingRoom", "/swagger-ui/**", "/api/login", "/api/allUser", "/api/logout",
                                "/api/validateToken", "/api/getMeetingRoomId", "/api/login/google", "/ws/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // 允許所有來源
        configuration.addAllowedMethod("*"); // 允許所有 HTTP 方法
        configuration.addAllowedHeader("*"); // 允許所有標頭
        configuration.setAllowCredentials(true); // 允許攜帶 Cookie
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
