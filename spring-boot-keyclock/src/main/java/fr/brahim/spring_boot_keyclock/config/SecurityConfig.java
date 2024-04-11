package fr.brahim.spring_boot_keyclock.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    /**
     * Définit le décodeur JWT pour valider et décoder les jetons JWT.
     *
     * @param jwkSetUri L'URI du jeu de clés JSON Web Key (JWK) à utiliser pour la vérification du jeton.
     * @return Le décodeur JWT configuré.
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${spring.security.filter.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri) {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    /**
     * Configure la chaîne de filtres de sécurité HTTP.
     *
     * @param httpSecurity Le constructeur pour configurer la sécurité HTTP.
     * @return La chaîne de filtres de sécurité configurée.
     * @throws Exception Si une erreur survient lors de la configuration de la sécurité HTTP.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csfr -> csfr.disable())
                .authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
