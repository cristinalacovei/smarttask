package com.smarttask.task_service.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(token -> {

            Map<String, Object> realmAccess = token.getClaim("realm_access");

            if (realmAccess == null) {
                return Collections.emptyList();
            }

            Object rolesObject = realmAccess.get("roles");

            if (!(rolesObject instanceof Collection<?> roles)) {
                return Collections.emptyList();
            }

            return roles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(role -> (GrantedAuthority)
                            new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
        });

        return converter.convert(jwt);
    }
}