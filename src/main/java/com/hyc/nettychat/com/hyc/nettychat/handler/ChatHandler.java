package com.hyc.nettychat.com.hyc.nettychat.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyc.nettychat.chat.ChatManager;
import com.hyc.nettychat.user.User;
import com.hyc.nettychat.user.UserManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Date;

/**
 * 服务器消息收发处理类
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    /** 注册命令 */
    private static final String register = "register";
    /** 登录命令 */
    private static final String login = "login";
    /** 退出命令 */
    private static final String exit = "exit";
    /** 用户信息更新命令 */
    private static final String update = "update";
    /** 通道更新命令 */
    private static final String updateChannel = "updateChannel";
    /** 在线用户名单命令 */
    private static final String getAllOnlineUser = "getAllUser";
    /** 消息传输命令 */
    private static final String message = "msg";

    /**
     * 用户信息管理类
     */
    private UserManager userManager;
    /**
     * 聊天管理类
     */
    private ChatManager chatManager;

    /**
     * 初始化
     * @param userManager
     *      用户信息管理
     * @param chatManager
     *      聊天管理
     */
    public ChatHandler(UserManager userManager, ChatManager chatManager) {
        this.userManager = userManager;
        this.chatManager = chatManager;
    }

    /**
     * 消息处理
     * @param channelHandlerContext
     *      通道数据
     * @param textWebSocketFrame
     *      消息数据
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        // 读取WebSocket消息
        String msg = textWebSocketFrame.text();
        System.out.println(new Date().toString() + " from " + channelHandlerContext.channel().remoteAddress() + " " + msg);
        JSONObject fromClient = JSON.parseObject(msg);

        // 读取消息命令
        String op = (String) fromClient.get("op");

        // 消息发送
        if (op.equals(message)) {
            String receiver = (String) fromClient.get("receiver");

            JSONObject toClient = new JSONObject();
            toClient.put("op", message);
            toClient.put("sender", fromClient.get("sender"));
            toClient.put("receiver", fromClient.get("receiver"));
            toClient.put("msg", fromClient.get("msg"));

            if (receiver.equals("all")) {
                chatManager.broadcastMessage(toClient.toJSONString());
            } else {
                chatManager.sendMessage(toClient.toJSONString(), receiver);
            }
        }

        // 注册请求
        if (op.equals(register)) {
            String username = (String) fromClient.get("username");
            String password = (String) fromClient.get("password");
            String email = (String) fromClient.get("email");
            String gender = (String) fromClient.get("gender");

            JSONObject toClient = new JSONObject();
            toClient.put("op", register);
            if (userManager.addUser(username, password, email, gender)) {
                toClient.put("result", "success");
            } else {
                toClient.put("result", "false");
            }

            channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(toClient.toJSONString()));
            System.out.println(new Date().toString() + " to   " + channelHandlerContext.channel().remoteAddress() + " " + toClient.toJSONString());

        }

        // 用户更新请求
        if (op.equals(update)) {
            String username = (String) fromClient.get("username");
            String password = (String) fromClient.get("password");
            String email = (String) fromClient.get("email");
            String gender = (String) fromClient.get("gender");

            JSONObject toClient = new JSONObject();
            toClient.put("op", update);
            userManager.updateUser(username, password, email, gender);
            User user = userManager.getUser(username);
            if (user != null) {
                toClient.put("result", "success");
                toClient.put("user", user);
            } else {
                toClient.put("result", "false");
            }
            channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(toClient.toJSONString()));
            System.out.println(new Date().toString() + " to   " + channelHandlerContext.channel().remoteAddress() + " " + toClient.toJSONString());

        }

        // 登录请求
        if (op.equals(login)) {
            String username = (String) fromClient.get("username");
            String password = (String) fromClient.get("password");

            JSONObject toClient = new JSONObject();
            toClient.put("op", login);
            User user = userManager.loginUser(username, password);
            if (user != null) {
                if (chatManager.addChatUser(channelHandlerContext.channel(), user)) {
                    toClient.put("result", "success");
                    toClient.put("user", user);
                } else {
                    toClient.put("result", "false");
                }
            } else {
                toClient.put("result", "false");
            }

            channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(toClient.toJSONString()));
            System.out.println(new Date().toString() + " to   " + channelHandlerContext.channel().remoteAddress() + " " + toClient.toJSONString());

        }

        // 登出请求
        if (op.equals(exit)) {
            chatManager.removeChatUser(channelHandlerContext.channel());
            chatManager.broadcastChatUser();
        }

        // 通道更新请求
        if (op.equals(updateChannel)) {
            chatManager.updateChannel((String) fromClient.get("username"), channelHandlerContext.channel());
        }

        // 拉取在线名单请求
        if (op.equals(getAllOnlineUser)) {
            JSONObject toClient = new JSONObject();
            toClient.put("op", getAllOnlineUser);
            toClient.put("onlineUsers", chatManager.getAllUser());
            channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(toClient.toJSONString()));
            System.out.println(new Date().toString() + " to   " + channelHandlerContext.channel().remoteAddress() + " " + toClient.toJSONString());
        }
    }
}
