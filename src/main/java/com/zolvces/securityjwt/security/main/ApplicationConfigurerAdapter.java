package com.zolvces.securityjwt.security.main;

import com.zolvces.securityjwt.security.jwt.JwtHeadFilter;
import com.zolvces.securityjwt.security.user.JwtAuthenticationProvider;
import com.zolvces.securityjwt.security.user.JwtLoginFilter;
import com.zolvces.securityjwt.security.user.JwtUserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//////////////////////////////////////////
//////////////////////////////////////////
/**
 * spring security核心
 */
//////////////////////////////////////////
//////////////////////////////////////////

/**
 * @author aki
 * @date 2019/4/2 22:58.
 */
@Configuration
public class ApplicationConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private RsaVerifier verifier;

    @Autowired
    private RsaSigner signer;

    @Autowired
    private JwtUserDetailServiceImpl jwtUserDetailService;

    @Autowired
    @Qualifier(value = "myPassWordEncoder")
    private PasswordEncoder passwordEncoder;

    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    @Qualifier(value = "myAuthenticationEntryPoint")
    public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    private AccessDeniedHandler accessDeniedHandler;
    @Autowired
    @Qualifier(value = "myAccessDeniedHandler")
    public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //登录过滤器
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter();
        jwtLoginFilter.setAuthenticationManager(this.authenticationManagerBean());

        //登录成功和失败的操作
        LoginSuccessHandler loginSuccessHandler = new LoginSuccessHandler();
        loginSuccessHandler.setSigner(signer);
        jwtLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        jwtLoginFilter.setAuthenticationFailureHandler(new LoginFailureHandler());

        //登录过滤器的授权提供者(就这么叫吧)
        //调用JwtAuthenticationProvider进行登录校验
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(jwtUserDetailService);

        //JWT校验过滤器
        JwtHeadFilter headFilter = new JwtHeadFilter();
        headFilter.setVerifier(verifier);

        http
                //身份验证入口,当需要登录却没登录时调用
                //具体为,当抛出AccessDeniedException异常时且当前是匿名用户时调用
                //匿名用户: 当过滤器链走到匿名过滤器(AnonymousAuthenticationFilter)时,
                //会进行判断SecurityContext是否有凭证(Authentication),若前面的过滤器都没有提供凭证,
                //匿名过滤器会给SecurityContext提供一个匿名的凭证(可以理解为用户名和权限为anonymous的Authentication),
                //这也是JwtHeadFilter发现请求头中没有jwtToken不作处理而直接进入下一个过滤器的原因
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                //拒绝访问处理,当已登录,但权限不足时调用
                //抛出AccessDeniedException异常时且当不是匿名用户时调用
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .authorizeRequests()
                // 此动态url过滤器会比权限先执行
                .anyRequest().access("@accessDecisionService.hasPermission(request , authentication)")
                .and()
                //将授权提供者注册到授权管理器中(AuthenticationManager)
                .authenticationProvider(provider)
                .addFilterAfter(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(headFilter, JwtLoginFilter.class)
                //禁用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable();
    }


    @Bean(name = "myPassWordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
