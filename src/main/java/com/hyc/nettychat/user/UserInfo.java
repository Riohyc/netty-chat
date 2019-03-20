package com.hyc.nettychat.user;

/**
 * 用户信息
 * 仅包含ID和用户名）
 * 用于广播在线用户名单
 */
public class UserInfo {
    /** ID */
    private int id;
    /** 用户名 */
    private String username;

    public UserInfo(User user){
        this.id = user.getId();
        this.username = user.getUsername();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
