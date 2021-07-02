package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.domain.TblUser;
import com.bjpowernode.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

/**
 * 2021/6/4
 */
public interface UserService {
    //按user主键查询用户
    public TblUser selectUserById(String id);

    //按用户名和密码查询用户
    User queryUserByLoginAndPwd(Map<String,Object> map);
    //查询所有用户
    List<User> queryAllUsers();
}
