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
import teclan.netty.coding.FileInfoDecoder;
import teclan.netty.coding.FileInfoEnCoder;
import teclan.netty.handler.FileServerHanlder;

import java.io.IOException;

public class FileServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServer.class);

    private FileServerHanlder fileServerHanlder;

    public FileServer() {
        fileServerHanlder = new FileServerHanlder();
    }

    public FileServer(int pollSize) {
        fileServerHanlder = new FileServerHanlder(pollSize);
    }

    public void run(int port) throws InterruptedException, IOException {
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
                        ch.pipeline().addLast(fileServerHanlder);
                    }
                });
        ChannelFuture f = serverBootstrap.bind(port).sync();//邦定端口并启动
        LOGGER.info("文件服务器已经启动，端口号：{}", port);
        fileServerHanlder.run();
        f.channel().closeFuture().sync();
    }
}
