package cn.smile67.netty.c3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.Buffer;
import java.nio.charset.Charset;

@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                // 分工细分 Boss 和 Worker
                // boss只负责ServerSocketChannel 上的 accept 事件 worker 只负责 SocketChannel 上的读写
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug(buf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .bind(8080);
    }
//    public static void main(String[] args) {
//        // 细分2：创建一个独立的 EventLoopGroup
//        EventLoopGroup group = new DefaultEventLoopGroup();
//        new ServerBootstrap()
//                // boss 和 worker
//                // 细分1：boss 只负责 ServerSocketChannel 上 accept 事件     worker 只负责 socketChannel 上的读写
//                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
//                .channel(NioServerSocketChannel.class)
//                .childHandler(new ChannelInitializer<NioSocketChannel>() {
//                    @Override
//                    protected void initChannel(NioSocketChannel ch) throws Exception {
//                        ch.pipeline().addLast("handler1", new ChannelInboundHandlerAdapter() {
//                            @Override                                         // ByteBuf
//                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                ByteBuf buf = (ByteBuf) msg;
//                                log.debug(buf.toString(Charset.defaultCharset()));
//                                ctx.fireChannelRead(msg); // 让消息传递给下一个handler
//                            }
//                        });
//                        /*.addLast(group, "handler2", new ChannelInboundHandlerAdapter() {
//                            @Override                                         // ByteBuf
//                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                ByteBuf buf = (ByteBuf) msg;
//                                log.debug(buf.toString(Charset.defaultCharset()));
//                            }
//                        });*/
//                    }
//                })
//                .bind(8080);
//    }
}
