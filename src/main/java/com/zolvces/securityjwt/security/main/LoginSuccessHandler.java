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
 * @author aki
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
        // 可以在这里改一些参数 比如塞入时间，在查询用户的时候(JwtUserDetailServiceImpl)已经塞过了
//        hashMap.put("exp", new DateTime().getMillis());
        String token = JwtHelper.encode(gson.toJson(hashMap), signer).getEncoded();
        //签发token
        response.getWriter().write("token="+token);
    }

    public void setSigner(RsaSigner signer) {
        this.signer = signer;
    }
}
