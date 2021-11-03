package com.zolvces.securityjwt.security.simple;

import com.alibaba.fastjson.JSON;
import org.joda.time.DateTime;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//////////////////////////////////////////
//////////////////////////////////////////
/**
 * 验证token 报文头携带 Authentication
 */
//////////////////////////////////////////
//////////////////////////////////////////

/**拦截请求进行token验证
 *
 * JwtHeadFilter 实现token校验的核心,这是自定义的过滤器,主要是请求通过过滤器时,会对其携带的token进行解析和校验
 * 1.获取请求中携带的token
 * 2.若没有获取到token则return,调交给接下来的过滤器链处理
 * 3.若有token,但是校验失败,进行校验失败处理
 * 4.若token校验成功,通过从token中获取的用户信息生成一个凭证(Authentication),并放置到SecurityContext
 *
 * @author niXueChao
 * @date 2019/4/3 15:03.
 */
public class JwtHeadFilter extends OncePerRequestFilter {
    private RsaVerifier verifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authentication");
        if (token == null || token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        JwtUser user;
        try {
            Jwt jwt = JwtHelper.decodeAndVerify(token, verifier);
            String claims = jwt.getClaims();
            user = JSON.parseObject(claims, JwtUser.class);
            // 当前时刻再 过期时间戳之后 1 分钟过期
            // 应该写一个刷新时间戳的方法
            if (new DateTime().minusMinutes(1).isAfter(new DateTime(user.getExp()))) {
                throw new RuntimeException("时间戳过期了");
            }
            //todo: 可以在这里添加检查用户是否过期,冻结...
        } catch (Exception e) {
            //这里也可以filterChain.doFilter(request,response)然后return,那最后就会调用
            //.exceptionHandling().authenticationEntryPoint,也就是本列中的"需要登陆"
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("token 失效" + e.getMessage());
            return;
        }
        JwtLoginToken jwtLoginToken = new JwtLoginToken(user, "", user.getAuthorities());
        jwtLoginToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(jwtLoginToken);
        filterChain.doFilter(request, response);
    }


    public void setVerifier(RsaVerifier verifier) {
        this.verifier = verifier;
    }
}
