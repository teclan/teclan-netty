package teclan.netty.udp.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class FileServer {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileServer.class);

    public void run(int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();

            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch)
                                throws Exception {
                            // TODO Auto-generated method stub
                            ch.pipeline().addLast(new FileServerHandler());

                        }
                    });

            LOGGER.info("start file server at port:" + port);

            b.bind(port).sync().channel().closeFuture().await();

        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        new FileServer().run(port);

    }

}
