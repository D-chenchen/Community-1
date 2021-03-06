package com.community.service;

import com.community.entity.DiscussPost;
import com.community.entity.LoginTicket;
import com.community.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    /**
     * 根据用户 id 查询用户
     */
    User selectById(int id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    User selectByEmail(String email);

    /**
     * 添加用户
     */
    int insertUser(User user);

    /**
     * 注册用户
     */
    Map<String, Object> register(User user);

    /**
     * 修改用户状态
     */
    int updateStatus(int userId, int status);

    /**
     * 激活用户
     */
    int activation(int userId, String code);

    /**
     * 用户登录
     */
    Map<String, Object> login(String username, String password, int expired);

    /**
     * 用户退出
     */
    void logout(String ticket);

    /**
     * 根据 ticket 查询用户
     */
    LoginTicket selectByTicket(String ticket);

    /**
     * 更新用户头像
     */
    int updateHeaderUrl(int userId, String avatarUrl);

    /**
     * 修改密码
     */
    Map<String, Object> changePassword(int id, String oldPassword, String newPassword, String confirmPassword);
}
