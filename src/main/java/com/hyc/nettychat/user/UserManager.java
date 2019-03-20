package com.hyc.nettychat.user;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息管理类
 */
public class UserManager {
    /**
     * 用户信息表
     */
    private List<User> userList;

    /**
     * 初始化
     * 添加默认用户root与hyc
     * root为管理员权限
     */
    public UserManager() {
        this.userList = new ArrayList<User>();
        User administrator = new User("root", "root", "root@root.com", "N/A");
        administrator.setId(0);
        administrator.setLevel(0);
        User newUser = new User("hyc", "hyc", "hyc@hyc.com", "male");
        newUser.setId(1);
        newUser.setLevel(1);
        userList.add(administrator);
        userList.add(newUser);
    }

    /**
     * 根据用户名获得用户信息
     * @param username
     *      用户名
     * @return
     *      用户信息
     */
    public User getUser(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * 添加新用户
     * @param username
     *      用户名
     * @param password
     *      密码
     * @param email
     *      邮箱
     * @param gender
     *      性别
     * @return
     *      是否添加成功
     */
    public boolean addUser(String username, String password, String email, String gender) {
        if (username.equals("all")) {
            return false;
        }
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }
        User newUser = new User(username, password, email, gender);
        newUser.setLevel(1);
        newUser.setId(userList.size());
        userList.add(newUser);
        return true;
    }

    /**
     * 更新用户信息
     * @param username
     *      用户名
     * @param password
     *      密码
     * @param email
     *      邮箱
     * @param gender
     *      性别
     */
    public void updateUser(String username, String password, String email, String gender) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                user.setPassword(password);
                user.setEmail(email);
                user.setGender(gender);
            }
        }
    }

    /**
     * 用户登录信息验证
     * @param username
     *      用户名
     * @param password
     *      密码
     * @return
     *      用户信息
     */
    public User loginUser(String username, String password) {
        for (User user : userList) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
