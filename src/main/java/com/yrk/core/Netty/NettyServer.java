package com.yrk.core.Netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import sun.nio.ch.Net;

import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: 基于Netty完成简易聊天室
 * @Author: ruikun
 * @CreateTime: 2022-11-02  21:58
 * @Description: TODO
 * @Version: 1.0
 */
public class NettyServer {

    public static void main(String[] args) {
        /**
         * netty 核心结构 一主多子   意思是一个连接监听  多个时间执行 雷同开辟多个线程增加网络交互效率
         **/


        //1。生成一个主时间监听 并设置数量
        EventLoopGroup boosLoop=new NioEventLoopGroup(1);
        //2.生成多个子 事件监听
        EventLoopGroup subLoop=new NioEventLoopGroup(8);

        try {
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            //绑定两个组 ？？？
            serverBootstrap.group(boosLoop,subLoop)
                    //设置通信通道
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //设置解密
                            pipeline.addLast("decoder",new StringDecoder());
                            //设置返回的编码  这里可以自己自定义请求 以及响应体
                            pipeline.addLast("encoder",new StringEncoder());
                            //设置自己定义的事件处理器
                            pipeline.addLast(new NettyServerHandler());
                        }
                    }


                   /*     //3.设置解码 以及事件监听类
                        @Override
                        protected void initChannel(NioServerSocketChannel nioServerSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioServerSocketChannel.pipeline();
                            //设置解密
                            pipeline.addLast("decoder",new StringDecoder());
                            //设置返回的编码  这里可以自己自定义请求 以及响应体
                            pipeline.addLast("encoder",new StringEncoder());
                            //设置自己定义的事件处理器
                            pipeline.addLast(new NettyServerHandler());
                        }
                    }*/
                    );
            System.out.println("服务启动");
            //4.将对外的网络通道端口监听交于 主节点完成  由主节点将所有的通信通道中的连接注册进入子节点的事件监听中
            ChannelFuture channelFuture = serverBootstrap.bind(8082).sync();

            System.out.println("服务启动完成");
            channelFuture.channel().closeFuture().sync();
            System.out.println("服务关闭完成");
        }catch (Exception e){

        }finally {
            boosLoop.shutdownGracefully();
            subLoop.shutdownGracefully();
        }


    }
}
