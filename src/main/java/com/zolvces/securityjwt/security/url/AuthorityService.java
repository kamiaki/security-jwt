package com.zolvces.securityjwt.security.url;


import java.util.List;

public interface AuthorityService {
    /**
     * 获取所有权限
     * @return
     */
    List<AuthorityParm> getAllAuthorities();
}
