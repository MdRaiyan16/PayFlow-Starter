package com.payflow.gateway.security;

import com.payflow.gateway.auth.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * User Roles
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority(user.getRole().name())
        );
    }

    /**
     * Password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Username
     * We use Email as Username
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Account Not Expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return user.getAccountNonExpired();
    }

    /**
     * Account Not Locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked();
    }

    /**
     * Credentials Not Expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return user.getCredentialsNonExpired();
    }

    /**
     * User Enabled
     */
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

    /**
     * Return Complete User
     */
    public User getUser() {
        return user;
    }

}