package com.zolvces.securityjwt.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.zolvces.securityjwt.security.url.AuthorityParm;
import com.zolvces.securityjwt.security.url.AuthorityService;
import com.zolvces.securityjwt.security.url.DynamicUrl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author aki
 * @date 2019/4/2 23:34.
 */
@RestController
public class TestController {


    /**任何人都能访问
     * @return
     */
    @GetMapping("/publicMsg")
    public String getMsg(){
        return "you get the message!";
    }

    /**登录的用户才能访问
     * @return
     */
    @GetMapping("/innerMsg")
    public String innerMsg(){
        return "you get the message!";
    }

    /**管理员(admin)才能访问
     * @return
     */
    @GetMapping("/secret")
    public String secret(){
        return "you get the message!";
    }


    @Autowired
    private RsaSigner signer;

    /**
     * 刷新令牌
     * @param authentication
     * @return
     */
    @GetMapping("/refreshToken")
    public String refreshToken(Authentication authentication){
            Gson gson = new Gson();
            String userJsonStr = JSON.toJSONString(authentication.getPrincipal());
            Map hashMap = gson.fromJson(userJsonStr, Map.class);
            // 这里不用校验用户名和密码了，之前已经校验过了，不过要放行刷新token方法。
            // 刷新日期
            hashMap.put("exp", new DateTime().getMillis());
            String token = JwtHelper.encode(gson.toJson(hashMap), signer).getEncoded();
            //签发token
            return "token="+token;
    }


    @Autowired
    @Qualifier(value = "myDynamicUrl")
    private DynamicUrl dynamicUrl;
    /**
     * 获取权限信息, url不存在就有权限，存在判断有无权限
     *
     * @param response
     * @param authentication
     * @param urlName
     * @return
     */
    @RequestMapping(value = "getAuthority")
    public String getAuthority(HttpServletResponse response, Authentication authentication, String urlName) {
        boolean isPass = dynamicUrl.getAuthoritylUrlByUrlName(urlName, authentication);
        if (isPass) {
            response.setStatus(HttpStatus.OK.value());
            return "success";
        }else{
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "403";
        }
    }

}
