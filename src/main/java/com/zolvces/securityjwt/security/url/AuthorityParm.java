package com.zolvces.securityjwt.security.url;

import lombok.Data;

@Data
public class AuthorityParm {
    private String url;
    private String[] Authorities;   //null为不需要权限
}
