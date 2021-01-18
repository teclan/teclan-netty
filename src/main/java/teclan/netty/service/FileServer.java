package teclan.netty.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.coder.FileInfoDecoder;
import teclan.netty.coder.FileInfoEnCoder;
import teclan.netty.handler.FileServerHanlder;

import java.io.IOException;

public class FileServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServer.class);

    public void run(int port)  {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel ch) throws Exception {
                        LOGGER.info("客户端接入：{} ==> {}", ch.remoteAddress(), ch.localAddress().toString());
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new FileInfoEnCoder());
                        ch.pipeline().addLast(new FileInfoDecoder());
                        ch.pipeline().addLast(new FileServerHanlder());
                    }
                });

        try {
            ChannelFuture f = serverBootstrap.bind(port).sync();//邦定端口并启动
            LOGGER.info("文件服务器已经启动，端口号：{}", port);
            FileServerHanlder.run();
            f.channel().closeFuture().sync();
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }

    public static void push(final String remote,final String srcDir, final String dstDir, final String fileName) throws Exception {
        FileServerHanlder.push(remote, srcDir, dstDir, fileName);
    }
}
