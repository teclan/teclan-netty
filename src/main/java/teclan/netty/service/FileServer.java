package teclan.netty.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.coding.FileInfoDecoder;
import teclan.netty.coding.FileInfoEnCoder;
import teclan.netty.handler.FileServerHanlder;

public class FileServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServer.class);

    public void run(int port) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap ();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.SO_BACKLOG, 10240)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                      protected void initChannel(Channel ch) throws Exception {
                        LOGGER.info("客户端接入：{} ==> {}",ch.remoteAddress(),ch.localAddress().toString());
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new FileInfoEnCoder());
                        ch.pipeline().addLast(new FileInfoDecoder());
                        ch.pipeline().addLast(new FileServerHanlder());

//                          ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8))
//                                  .addLast(new LineBasedFrameDecoder(8192))
//                                  .addLast(new StringDecoder(CharsetUtil.UTF_8))
//                                  .addLast(new ChunkedWriteHandler())
//                                  .addLast(new FileServerHanlder());
                    }
                });
        ChannelFuture f = serverBootstrap.bind(port).sync();//邦定端口并启动
       LOGGER.info("文件服务器已经启动，端口号：{}",port);
        f.channel().closeFuture().sync();
    }
}
