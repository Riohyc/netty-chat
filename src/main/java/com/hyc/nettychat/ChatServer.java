package com.hyc.nettychat;

import com.hyc.nettychat.chat.ChatManager;
import com.hyc.nettychat.com.hyc.nettychat.handler.ChatHandler;
import com.hyc.nettychat.user.UserManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 服务器启动类
 *
 * 默认端口8080
 */
public class ChatServer {
    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        final UserManager userManager = new UserManager();
        final ChatManager chatManager = new ChatManager();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            System.out.println("建立通道：" + socketChannel.remoteAddress());
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(65535));
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new WebSocketServerProtocolHandler("/nettychat"));
                            pipeline.addLast(new ChatHandler(userManager, chatManager));

                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("服务器启动 监听端口:" + port + "...");
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();

        }
    }

    public static void main(String args[]) throws InterruptedException {
        int port = 8080;
        if (args.length>0){
            port = Integer.valueOf(args[0]);
        }
        ChatServer chatServer = new ChatServer(port);
        chatServer.run();
    }
}

