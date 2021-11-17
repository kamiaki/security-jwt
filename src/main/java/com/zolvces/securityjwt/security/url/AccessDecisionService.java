package com.zolvces.securityjwt.security.url;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

//////////////////////////////////////////
//////////////////////////////////////////
/**
 * 动态url
 */
//////////////////////////////////////////
//////////////////////////////////////////

/**
 * 配置路径访问限制,若你的用户角色比较简单,不需要存数据库,
 * 可以在ApplicationConfigurerAdapter里配置如
 * httpSecurity
 * .authorizeRequests()
 * .antMatchers("/order").....
 *
 * @author aki
 * @date 2019/4/10 10:33.
 */
@Component("accessDecisionService")
public class AccessDecisionService {
    // 匹配方法 防止重复调用
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    // 获取权限方法
    @Autowired
    @Qualifier(value = "staticAuthority")
    private AuthorityService authorityService;

    @Autowired
    @Qualifier(value = "myDynamicUrl")
    private DynamicUrl dynamicUrl;

    public boolean hasPermission(HttpServletRequest request, Authentication auth) {
        String requestURI = request.getRequestURI();
        //匹配到url并且权限控制不为null, 才进行下一步拦截判断, 其余全部放行.
        boolean noHasOrNull = true;
        List<AuthorityParm> allAuthorities = authorityService.getAllAuthorities();
        for (AuthorityParm authorityParm : allAuthorities) {
            if (antPathMatcher.match(authorityParm.getUrl(), requestURI) && null != authorityParm.getAuthorities()) {
                noHasOrNull = false;
                break;
            }
        }
        if (noHasOrNull) return true;
        // 匿名身份验证令牌
        if (auth instanceof AnonymousAuthenticationToken) return false;
        // 判断是否放行
        boolean authoritylUrl = dynamicUrl.getAuthoritylUrlByUrlName(requestURI, auth);
        return authoritylUrl;
    }
}
