package com.zolvces.securityjwt.security.user;

import com.zolvces.securityjwt.security.jwt.JwtLoginToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//////////////////////////////////////////
//////////////////////////////////////////
/**
 *  登录 获取用户名密码
 */
//////////////////////////////////////////
//////////////////////////////////////////

/**
 * JwtLoginFilter 自定义的登录过滤器,把它加到SpringSecurity的过滤链中,拦截登录请求它干的事有
 * 1 设置登录的url,请求的方式,其实也就是定义这个过滤器要拦截哪个请求
 * 2 调用JwtAuthenticationProvider进行登录校验
 * 3 校验成功调用LoginSuccessHandler,校验失败调用LoginSuccessHandler
 * @author aki
 * @date 2019/4/2 14:07.
 */
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 设置登录的url,请求的方式,其实也就是定义这个过滤器要拦截哪个请求
     */
    public JwtLoginFilter() {
        super(new AntPathRequestMatcher("/jwtLogin", "POST"));
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
       try {
           String userName = request.getParameter("userName");
           String password = request.getParameter("password");
           //创建未认证的凭证(etAuthenticated(false)),注意此时凭证中的主体principal为用户名
           JwtLoginToken jwtLoginToken = new JwtLoginToken(userName, password);
           //将认证详情(ip,sessionId)写到凭证
           jwtLoginToken.setDetails(new WebAuthenticationDetails(request));
           //AuthenticationManager获取受支持的AuthenticationProvider(这里也就是JwtAuthenticationProvider),
           //生成已认证的凭证,此时凭证中的主体为userDetails
           Authentication authenticatedToken = this.getAuthenticationManager().authenticate(jwtLoginToken);
           return authenticatedToken;
       }catch (Exception e){
           throw new BadCredentialsException("坏的凭证");
       }
    }

}
