package com.sam.BankingWebApplication1.Security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class KycPrincipal implements UserDetails {

    @Getter
    private final Long customerId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public KycPrincipal(Long customerId,
                        String email,
                        Collection<? extends GrantedAuthority> authorities) {
        this.customerId = customerId;
        this.email = email;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return email; }

}
