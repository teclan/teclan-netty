package teclan.netty.tcp.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class FileClient {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileClient.class);

    private static FileClientHandler handler = new FileClientHandler();

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(handler);
                }
            });

            ChannelFuture f = b.connect(host, port).sync();
            LOGGER.info("The Clinet is start!");

            // 请求文件
            handler.requestFile("/home/dev/db2.sql");

            f.channel().closeFuture().sync();
            LOGGER.info("The Clinet is stop!");
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
