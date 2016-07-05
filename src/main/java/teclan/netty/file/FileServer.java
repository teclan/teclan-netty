package teclan.netty.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class FileServer {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileServer.class);

    private int port      = 8080;
    private int maxLength = 8192;

    public FileServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(new FileServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // b.group(bossGroup, workerGroup)
            // .channel(NioServerSocketChannel.class)
            // .option(ChannelOption.SO_BACKLOG, 100)
            // .childOption(ChannelOption.SO_KEEPALIVE, true)
            // .handler(new LoggingHandler(LogLevel.INFO))
            // .childHandler(new ChannelInitializer<SocketChannel>() {
            // @Override
            // public void initChannel(SocketChannel ch)
            // throws Exception {
            // ch.pipeline()
            // .addLast(new StringEncoder(
            // CharsetUtil.UTF_8))
            // .addLast(new LineBasedFrameDecoder(
            // maxLength))
            // .addLast(new StringDecoder(
            // CharsetUtil.UTF_8))
            // .addLast(new FileServerHandler());
            //
            // }
            // });

            ChannelFuture f = b.bind(port).sync();

            LOGGER.info("The Server is start on port:{}", port);

            f.channel().closeFuture().sync();
            LOGGER.info("The Server is stop");
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new FileServer(port).run();
    }

}
