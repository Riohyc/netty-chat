package com.hyc.nettychat.chat;

import com.alibaba.fastjson.JSONObject;
import com.hyc.nettychat.user.User;
import com.hyc.nettychat.user.UserInfo;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 聊天管理类
 */
public class ChatManager {
    /**
     * 在线用户表
     */
    private HashMap<UserInfo, Channel> chatUserMap;

    /**
     * 初始化
     * 新建在线用户表
     */
    public ChatManager() {
        this.chatUserMap = new HashMap<UserInfo, Channel>();
    }

    /**
     * 新增在线用户
     * @param channel
     *      用户通道
     * @param user
     *      用户信息
     * @return
     *      是否登录成功
     */
    public boolean addChatUser(Channel channel, User user) {
        for (UserInfo userInfo : chatUserMap.keySet()) {
            // 该用户已经登录，拒绝请求
            if (userInfo.getUsername().equals(user.getUsername())) {
                return false;
            }
        }
        chatUserMap.put(new UserInfo(user), channel);
        broadcastChatUser();
        return true;
    }

    /**
     * 移除在线用户
     * @param channel
     *      用户通道
     */
    public void removeChatUser(Channel channel) {
        UserInfo toBeRemoved = null;
        for (UserInfo userInfo : chatUserMap.keySet()) {
            if (channel == chatUserMap.get(userInfo)) {
                toBeRemoved = userInfo;
                break;
            }
        }
        chatUserMap.remove(toBeRemoved);
    }

    /**
     * 获得所有在线用户信息
     * @return
     *      用户信息（仅含用户名及ID）
     */
    public List<UserInfo> getAllUser() {
        List<UserInfo> chatUsers = new ArrayList<UserInfo>();
        for (UserInfo userInfo : chatUserMap.keySet()) {
            chatUsers.add(userInfo);
        }
        return chatUsers;
    }

    /**
     * 用户通道更新
     * @param username
     *      用户名
     * @param channel
     *      用户新通道
     */
    public void updateChannel(String username, Channel channel) {
        for (UserInfo userInfo : chatUserMap.keySet()) {
            if (userInfo.getUsername().equals(username)) {
                chatUserMap.put(userInfo, channel);
            }
        }
    }

    /**
     * 广播消息
     * @param msg
     *      消息
     */
    public void broadcastMessage(String msg) {
        for (UserInfo userInfo : chatUserMap.keySet()) {
            Channel channel = chatUserMap.get(userInfo);
            channel.writeAndFlush(new TextWebSocketFrame(msg));
            System.out.println(new Date().toString() + " to   " + channel.remoteAddress() + " " + msg);
        }
    }

    /**
     * 广播在线用户表
     */
    public void broadcastChatUser() {
        JSONObject toClient = new JSONObject();
        toClient.put("op", "getAllUser");
        toClient.put("onlineUsers", getAllUser());
        broadcastMessage(toClient.toJSONString());
    }

    /**
     * 向指定用户发送消息
     * @param msg
     *      消息
     * @param username
     *      用户名
     */
    public void sendMessage(String msg, String username) {
        for (UserInfo userInfo : chatUserMap.keySet()) {
            if (userInfo.getUsername().equals(username)) {
                chatUserMap.get(userInfo).writeAndFlush(new TextWebSocketFrame(msg));
                System.out.println(new Date().toString() + " to   " + chatUserMap.get(userInfo).remoteAddress() + " " + msg);
            }
        }
    }
}
