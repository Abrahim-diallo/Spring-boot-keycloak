package fr.brahim.spring_boot_keyclock.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Value("${jwt.auth.converter.principal-attribute}")
    private String principleAttribute;

    @Value("${jwt.auth.converter.resource-id}")
    private String clientId;

    /**
     * Convertit un jeton JWT en jeton d'authentification Spring Security.
     *
     * @param jwt Le jeton JWT à convertir.
     * @return Un jeton d'authentification Spring Security.
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(), extractResourceRoles(jwt).stream()).toList();
        return new JwtAuthenticationToken(jwt, authorities, getPrincipleName(jwt));
    }

    /**
     * Extrait les rôles de ressource du jeton JWT.
     *
     * @param jwt Le jeton JWT à analyser.
     * @return Les rôles de ressource extraits.
     */
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        if (jwt.getClaim("resource_access") == null) {
            return List.of();
        }

        resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess.get(clientId) == null) {
            return List.of();
        }

        resource = (Map<String, Object>) resourceAccess.get(clientId);

        if (resource.get("roles") == null) {
            return List.of();
        }

        resourceRoles = (Collection<String>) resource.get("roles");

        return resourceRoles.stream().map(role -> new SimpleGrantedAuthority("ROLE_".concat(role))).toList();
    }

    /**
     * Obtient le nom du principal à partir du jeton JWT.
     *
     * @param jwt Le jeton JWT contenant le nom du principal.
     * @return Le nom du principal.
     */
    private String getPrincipleName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;

        if (principleAttribute != null) {
            claimName = principleAttribute;
        }
        return jwt.getClaim(claimName);
    }
}
