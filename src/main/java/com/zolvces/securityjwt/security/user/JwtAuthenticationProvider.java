package com.zolvces.securityjwt.security.user;

import com.zolvces.securityjwt.security.jwt.JwtLoginToken;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

//////////////////////////////////////////
//////////////////////////////////////////
/**
 *  用户名密码校验器
 */
//////////////////////////////////////////
//////////////////////////////////////////

/**认证器
 *
 * JwtAuthenticationProvider 自定义的认证器,账号密码对不对等校验就是它干的,主要功能
 * 1.首先规定自己支持校验那种凭证(Authentication)
 * 2.进行用户校验,调用JwtUserDetailServiceImpl 查询当前用户(JwtUser),判断用户账号密码是否正确,用户是否过期,被锁定等等
 * 3.若用户校验失败则抛异常给JwtLoginFilter,JwtLoginFilter捕获异常调用登录失败的处理类(LoginFailureHandler)
 * 4.若用户校验成功,则生成一个已认证的凭证,也就是Authentication,对应本例的JwtLoginToken 并返回给JwtLoginFilter,JwtLoginFilter拿到凭证后调用登陆成功的处理类LoginSuccessHandler
 *
 * @author aki
 * @date 2019/4/2 15:59.
 */

public class JwtAuthenticationProvider implements AuthenticationProvider {

    /**供根据用户名查询用户,获取UserDetails的方法*/
    private UserDetailsService userDetailsService;

    /**提供加密方式,密码验证时,需要加密后进行对比*/
    private PasswordEncoder passwordEncoder;


    /** 认证提供者进行认证,注意这里传入的authentication对象,是JwtLoginFilter里调用
     * @see JwtLoginToken#JwtLoginToken(Object, Object) 方法生成的,是未认证状态的(setAuthenticated(false))
     * 此方法会返回一个已认证状态的authentication
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName= authentication.getName();
        //获取用户
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        //转换authentication 认证时会先调用support方法,受支持才会调用,所以能强转
        JwtLoginToken jwtLoginToken= (JwtLoginToken) authentication;
        //检查
        defaultCheck(userDetails);
        additionalAuthenticationChecks(userDetails,jwtLoginToken);
        //构造已认证的authentication
        JwtLoginToken authenticatedToken = new JwtLoginToken(userDetails, jwtLoginToken.getCredentials(), userDetails.getAuthorities());
        authenticatedToken.setDetails(jwtLoginToken.getDetails());
        return authenticatedToken;
    }

    /**这个provider支持哪种凭证(token)的认证*/
    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtLoginToken.class
                .isAssignableFrom(authentication));
    }

    /**(附加检查项)检查密码是否正确*/
    private void additionalAuthenticationChecks(UserDetails userDetails,
                                                  JwtLoginToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("Bad credentials");
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    /**用户默认检查,用户是否锁定过期等*/
    private void defaultCheck(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            throw new LockedException("User account is locked");
        }

        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("User account has expired");
        }
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
