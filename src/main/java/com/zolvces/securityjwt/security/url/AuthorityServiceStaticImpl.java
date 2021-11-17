package com.zolvces.securityjwt.security.url;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "staticAuthority")
public class AuthorityServiceStaticImpl implements AuthorityService {

    @Override
    public List<AuthorityParm> getAllAuthorities() {
        List<AuthorityParm> authList = new ArrayList<>();
        AuthorityParm authorityParm;

        /////////////////////////////////////////////////////////////////////////////////
        authorityParm = new AuthorityParm();
        authorityParm.setUrl("/publicMsg" + "**/**");
        authorityParm.setAuthorities(null);
        authList.add(authorityParm);

        /////////////////////////////////////////////////////////////////////////////////
        authorityParm = new AuthorityParm();
        authorityParm.setUrl("/refreshToken" + "**/**");
        authorityParm.setAuthorities(new String[]{"admin"});
        authList.add(authorityParm);

        /////////////////////////////////////////////////////////////////////////////////
        authorityParm = new AuthorityParm();
        authorityParm.setUrl("/innerMsg" + "**/**");
        authorityParm.setAuthorities(new String[]{"admin"});
        authList.add(authorityParm);

        /////////////////////////////////////////////////////////////////////////////////
        authorityParm = new AuthorityParm();
        authorityParm.setUrl("/secret" + "**/**");
        authorityParm.setAuthorities(new String[]{"admin"});
        authList.add(authorityParm);

        /////////////////////////////////////////////////////////////////////////////////
        authorityParm = new AuthorityParm();
        authorityParm.setUrl("/innerMsg" + "**/**");
        authorityParm.setAuthorities(new String[]{"user"});
        authList.add(authorityParm);


        return authList;
    }
}
