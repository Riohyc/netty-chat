package com.hyc.nettychat.user;

/**
 * 用户信息类
 */
public class User {
    /** ID */
    private int id;
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;
    /** 邮箱 */
    private String email;
    /** 性别 */
    private String gender;
    /** 权限等级 0：管理员 1：普通用户 */
    private int level;

    public User(String username, String password, String email, String gender){
        this.username = username;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.level = 1;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
