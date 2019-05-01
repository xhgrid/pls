package com.zwo.pls.security.service.impl;

import com.zwo.pls.security.domain.User;
import com.zwo.pls.security.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 一句话描述该类功能：
 * Created by Tony(黄记新) in 2019/4/27
 */
@Service
public class UserServiceDetailImpl implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserServiceDetailImpl.class);
    @Autowired
    private IUserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("认证开始，登录名："+username);
        User user = this.userService.selectByUserName(username);
        logger.info("认证结束，结果为："+user == null ? "":user.toString());
        return user;
    }
}
