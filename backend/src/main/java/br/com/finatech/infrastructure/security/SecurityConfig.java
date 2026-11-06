package br.com.finatech.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource; // Adicione este import

import br.com.finatech.infrastructure.hazelcast.RateLimitUserFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final SecurityFilter securityFilter;
        private final RateLimitUserFilter rateLimitFilter;
        private final CorsConfigurationSource corsConfigurationSource; // 1. Adicione a dependência do CORS

        // 2. Atualize o construtor para receber o corsConfigurationSource
        public SecurityConfig(SecurityFilter securityFilter, RateLimitUserFilter rateLimitFilter,
                        CorsConfigurationSource corsConfigurationSource) {
                this.securityFilter = securityFilter;
                this.rateLimitFilter = rateLimitFilter;
                this.corsConfigurationSource = corsConfigurationSource;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                // 3. Vincule explicitamente a sua configuração customizada de CORS
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/auth/login",
                                                                "/auth/register")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/api-docs/**",
                                                                "/api-docs.yaml",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/",
                                                                "/index.html",
                                                                "/styles.css",
                                                                "/app.js",
                                                                "/config.js",
                                                                "/manifest.webmanifest",
                                                                "/sw.js",
                                                                "/favicon.ico")
                                                .permitAll()

                                                .anyRequest().authenticated())

                                .addFilterBefore(
                                                securityFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                .addFilterAfter(
                                                rateLimitFilter,
                                                SecurityFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration authenticationConfiguration) {
                return authenticationConfiguration.getAuthenticationManager();
        }
}
