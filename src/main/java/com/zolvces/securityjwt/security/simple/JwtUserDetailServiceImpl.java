package com.zolvces.securityjwt.security.simple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

//////////////////////////////////////////
//////////////////////////////////////////
/**
 *  数据库拿用户权限 dao层的
 */
//////////////////////////////////////////
//////////////////////////////////////////

/**
 * @author niXueChao
 * @date 2019/4/8 11:26.
 */
@Component
public class JwtUserDetailServiceImpl implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public JwtUserDetailServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 模拟数据库查询
     * 这里靠 传入的 passwordEncoder 确定校验密码的方式
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 这里的内容会被添加 进 令牌， 但是 获取成map之后会有些错误 类型不对 不过数值是对的
        if ("admin".equals(username)) {
            return new JwtUser("admin", passwordEncoder.encode("123456"), new Date().getTime(), "admin");
        }
        if ("user".equals(username)) {
            return new JwtUser("user", passwordEncoder.encode("123456"), new Date().getTime(), "user");
        }
        if ("superadmin".equals(username)) {
            return new JwtUser("superadmin", passwordEncoder.encode("123456"), new Date().getTime(), "superadmin");
        }
        return null;
    }
}
