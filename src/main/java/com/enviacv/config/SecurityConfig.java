package com.enviacv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filtro(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(a -> a
                // O candidato envia o formulário sem login
                .requestMatchers(HttpMethod.POST, "/api/candidaturas").permitAll()
                // Qualquer outra rota da API exige o administrador
                .requestMatchers("/api/**").hasRole("ADMIN")
                // Página inicial, formulário e demais arquivos estáticos são públicos
                .anyRequest().permitAll())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService usuarios(
            @Value("${app.admin.user}") String usuario,
            @Value("${app.admin.password}") String senha,
            PasswordEncoder encoder) {
        UserDetails admin = User.withUsername(usuario)
                .password(encoder.encode(senha))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}