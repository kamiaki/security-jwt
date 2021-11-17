package com.zolvces.securityjwt.security.main;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

//////////////////////////////////////////
//////////////////////////////////////////
/**
 *  登录成功handler
 */
//////////////////////////////////////////
//////////////////////////////////////////

/**登录成功
 * @author niXueChao
 * @date 2019/3/12.
 */
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RsaSigner signer;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        String userJsonStr = JSON.toJSONString(authentication.getPrincipal());
        Map hashMap = gson.fromJson(userJsonStr, Map.class);
        // 可以在这里改一些参数 时间重写一下 因为读出来的可能不对
//        hashMap.put("exp", new DateTime().getMillis());
        String token = JwtHelper.encode(gson.toJson(hashMap), signer).getEncoded();
        //签发token
        response.getWriter().write("token="+token);
    }

    public void setSigner(RsaSigner signer) {
        this.signer = signer;
    }
}
