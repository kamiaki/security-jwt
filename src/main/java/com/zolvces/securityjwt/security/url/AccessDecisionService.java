package com.zolvces.securityjwt.security.url;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
 * @author niXueChao
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

    public boolean hasPermission(HttpServletRequest request, Authentication auth) {

        //匹配到url 并且 权限控制不为null, 才进行下一步拦截判断, 其余全部放行
        boolean noHas = true;
        List<AuthorityParm> allAuthorities = authorityService.getAllAuthorities();
        for (AuthorityParm authorityParm : allAuthorities){
            if (antPathMatcher.match(authorityParm.getUrl(), request.getRequestURI()) && null != authorityParm.getAuthorities()) {
                noHas = false;
                break;
            }
        }
        if (noHas) return true;


        if (auth instanceof AnonymousAuthenticationToken) {
            return false;
        }

        UserDetails user = (UserDetails) auth.getPrincipal();
//        String userName = user.getUsername();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        //根据用户名查出能访问哪些url, urls=findUrlByUserName()
//        List<String> urls = queryUrlByUserName(userName);
        List<String> urls = queryUrlByAuthorities(authorities);
        for (String url : urls) {
            if (antPathMatcher.match(url, request.getRequestURI())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 模拟数据库查询用户权限
     *
     * @param userName
     * @return
     */
    private List<String> queryUrlByUserName(String userName) {
        switch (userName) {
            case "admin":
                return Arrays.asList("/refreshToken", "/innerMsg", "/secret");
            case "user":
                return Arrays.asList("/innerMsg");
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 模拟数据库查询用户权限
     *
     * @param authorities
     * @return
     */
    private List<String> queryUrlByAuthorities(Collection<? extends GrantedAuthority> authorities) {
        // 数据库里查出的权限
        List list = new ArrayList();
        list.add(new SimpleGrantedAuthority("admin"));
        list.add(new SimpleGrantedAuthority("superadmin"));
        List list2 = new ArrayList();
        list2.add(new SimpleGrantedAuthority("user"));
        // 判断权限
        if (authorities == null || authorities.size() == 0) {
            return new ArrayList<>();
        } else if (list.containsAll(authorities)) {
            return Arrays.asList("/refreshToken", "/innerMsg", "/secret");
        } else if (authorities.contains(new SimpleGrantedAuthority("user"))) {
            return Arrays.asList("/innerMsg");
        } else {
            return new ArrayList<>();
        }
    }
}
