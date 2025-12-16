package com.yigit.ecommerce.security.context;

import com.yigit.ecommerce.exception.UnauthorizedException;
import com.yigit.ecommerce.security.user.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationContext {

    public Optional<CustomUserDetails> getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();
        return (principal instanceof CustomUserDetails cud) ? Optional.of(cud) : Optional.empty();
    }

    public CustomUserDetails requirePrincipal() {
        return getPrincipal()
                .orElseThrow(() -> new UnauthorizedException("Authentication required"));
    }

    public Long requireUserId() {
        return requirePrincipal().getId();
    }

    public String requireEmail() {
        return requirePrincipal().getUsername();
    }
}
