package com.zolvces.securityjwt.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author niXueChao
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
            // 刷新日期
            hashMap.put("exp", new DateTime().getMillis());
            String token = JwtHelper.encode(gson.toJson(hashMap), signer).getEncoded();
            //签发token
            return "token="+token;
    }
}
