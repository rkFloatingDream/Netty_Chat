package com.yrk.core.Netty;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

/**
 * @BelongsProject: Netty_demo
 * @Author: ruikun
 * @CreateTime: 2022-11-02  22:15
 * @Description: TODO
 * @Version: 1.0
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {


    //设置一个全局的事件监听器    自己写得古老版本用的是map去去存储 然后循环map输出 -。-
    private static ChannelGroup channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * @description:这里是通道连接的时候开始执行
     * @author: ruikun
     * @date: 2022/11/2 22:26
     * @param: [ctx]
     * @return: void
     **/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //拿到当前的管道对象  以及里面的值
        Channel channel = ctx.channel();
        //向整个事件分组中输出数据
        channelGroup.writeAndFlush(String.format("{0}:用户{1}上线",sd.format(new Date()),channel.localAddress()));
        //将连接的管道对象存入当前的全局事件中
        channelGroup.add(channel);

    }

    /**
     * @description: 读取数据
     * @author: ruikun
     * @date: 2022/11/2 22:40
     * @param: [ctx, msg]
     * @return: void
     **/
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //拿到当前的管道对象  以及里面的值
        Channel channel = ctx.channel();
        channelGroup.forEach(row->{
            if (channel.equals(row)){
                String format = sd.format(new Date())+":用户["+"自己]"+":发送了"+msg;
                row.writeAndFlush(format);
            }else {
                System.out.println("执行发送");
                String format = sd.format(new Date())+":用户["+channel.localAddress()+"]:发送了"+msg;
                row.writeAndFlush(format);
            }
        });


    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
