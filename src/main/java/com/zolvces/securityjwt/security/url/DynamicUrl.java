package com.zolvces.securityjwt.security.url;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

//////////////////////////////////////////
//////////////////////////////////////////
/**
 * 动态url 判断器 用于直接获取权限 和 安全框架获取权限
 */
//////////////////////////////////////////
//////////////////////////////////////////

@Component(value = "myDynamicUrl")
public class DynamicUrl {
    // 匹配方法 防止重复调用
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    // 获取权限方法
    @Autowired
    @Qualifier(value = "staticAuthority")
    private AuthorityService authorityService;


    /**
     *
     * @param urlName
     * @param authentication
     * @return
     */
    public boolean getAuthoritylUrlByUrlName(String urlName, Authentication authentication) {
        // 判断url是否存在
        boolean existUrl = false;
        // 遍历所有url权限配置项
        for (AuthorityParm authorityParm : authorityService.getAllAuthorities()) {
            //找url一致的权限配置项
            if (antPathMatcher.match(authorityParm.getUrl(), urlName)) {
                // url存在
                existUrl = true;
                // null表示不做权限控制,直接返回通过
                if (null == authorityParm.getAuthorities()) {
                    return true;
                } else {
                    // 这个url需要权限, 如果用户未登录, 就返回没有权限
                    if (null == authentication || !authentication.isAuthenticated()) {
                        return false;
                    }
                }
                //获取配置权限
                for (String configAuthority : authorityParm.getAuthorities()) {
                    //获取用户权限
                    for (GrantedAuthority userAuthority : authentication.getAuthorities()) {
                        //比对配置和用户权限
                        if (configAuthority.equals(userAuthority.getAuthority())) {
                            //返回路由成功
                            return true;
                        }
                    }
                }
            }
        }
        // 存在url匹配失败，不存在匹配成功
        if (!existUrl) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取我的AuthorityServiceStaticImpl 里的url
     *
     * @param request
     * @param authentication
     * @return
     */
    public boolean getAuthoritylUrl(HttpServletRequest request, Authentication authentication) {
        String urlName = request.getRequestURI();
        // 判断url是否存在
        boolean existUrl = false;
        // 遍历所有url权限配置项
        for (AuthorityParm authorityParm : authorityService.getAllAuthorities()) {
            //找url一致的权限配置项
            if (antPathMatcher.match(authorityParm.getUrl(), urlName)) {
                // url存在
                existUrl = true;
                // null表示不做权限控制,直接返回通过
                if (null == authorityParm.getAuthorities()) {
                    return true;
                } else {
                    // 这个url需要权限, 如果用户未登录, 就返回没有权限
                    if (null == authentication || !authentication.isAuthenticated()) {
                        return false;
                    }
                }
                //获取配置权限
                for (String configAuthority : authorityParm.getAuthorities()) {
                    //获取用户权限
                    for (GrantedAuthority userAuthority : authentication.getAuthorities()) {
                        //比对配置和用户权限
                        if (configAuthority.equals(userAuthority.getAuthority())) {
                            //返回路由成功
                            return true;
                        }
                    }
                }
            }
        }
        // 存在url匹配失败，不存在匹配成功
        if (!existUrl) {
            return true;
        } else {
            return false;
        }
    }
}
