package ge.batumi.tutormentor.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SecurityExpression {

    public boolean hasProgramRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            return false;
        }

        List<String> roles = jwtAuth.getToken()
                .getClaimAsStringList("program_roles");

        return roles != null && roles.contains(role);
    }
}