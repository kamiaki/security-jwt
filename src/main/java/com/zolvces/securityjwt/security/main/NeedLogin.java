package com.zolvces.securityjwt.security.main;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//////////////////////////////////////////
//////////////////////////////////////////
/**
 *  没登录 handler
 */
//////////////////////////////////////////
//////////////////////////////////////////
@Component(value = "myAuthenticationEntryPoint")
public class NeedLogin implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("需要登陆!");
    }
}
