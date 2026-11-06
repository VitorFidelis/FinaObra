package br.com.finatech.infrastructure.cors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Origens permitidas, lidas de cors.allowed-origins no application.yaml.
     * Configure via variável de ambiente CORS_ALLOWED_ORIGINS (vírgulas separam múltiplas origens).
     * Exemplo: CORS_ALLOWED_ORIGINS=https://meuapp.com,https://www.meuapp.com
     */
    @Value("${cors.allowed-origins}")
    private String allowedOriginsRaw;

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] origins = allowedOriginsRaw.split(",");
                registry.addMapping("/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Content-Type", "Authorization")
                        .maxAge(3600);
            }
        };
    }
}
