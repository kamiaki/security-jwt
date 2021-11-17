package com.zolvces.securityjwt.security.main;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//////////////////////////////////////////
//////////////////////////////////////////
/**
 *  没权限handler
 */
//////////////////////////////////////////
//////////////////////////////////////////
@Component(value = "myAccessDeniedHandler")
public class NoAuthority implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("没有权限!");
    }
}
