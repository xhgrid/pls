package com.zwo.pls.modules.system.service;

import com.zwo.pls.core.service.IBaseService;
import com.zwo.pls.modules.system.domain.User;

import java.util.List;

/**
 * 一句话描述该类功能：
 * Created by Tony(黄记新) in 2019/5/2
 */
public interface IUserService extends IBaseService<User> {
    /**
     * 批量插入用户。
     * @param users
     * @return
     */
    int insertBatch(List<User> users);
}
