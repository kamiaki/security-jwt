package com.zolvces.securityjwt.security.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//////////////////////////////////////////
//////////////////////////////////////////
/**
 *  这是用户对象 用来校验用户名密码和权限
 */
//////////////////////////////////////////
//////////////////////////////////////////

/**
 * @author aki
 * @date 2019/4/8 11:29.
 */
public class JwtUser implements UserDetails {
    private String username;
    private String password;
    private Long exp;
    private List<SimpleGrantedAuthority> authorities;

    public JwtUser() {
    }

    public JwtUser(String username, String password, Long exp, String ... roles) {
        this.username = username;
        this.password = password;
        this.exp = exp;
        this.authorities= Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(List<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }
}
