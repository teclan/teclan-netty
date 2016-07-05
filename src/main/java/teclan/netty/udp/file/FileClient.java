package teclan.netty.udp.file;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class FileClient {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileClient.class);

    public static String host = "127.0.0.1";
    public int           port = 8080;

    public void run(String host, int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch)
                                throws Exception {
                            // TODO Auto-generated method stub
                            ch.pipeline().addLast(new FileClientHandler());

                        }
                    });
            Channel ch = b.bind(host, port).sync().channel();

            ch.closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }

    public static InetSocketAddress getInetSocketAddress() {
        return new InetSocketAddress(host, 8080);
    }

    public static void main(String[] args) throws Exception {
        // 客户端的地址和端口，udp服务器和客户端不能指向相同IP和端口
        new FileClient().run("127.0.0.1", 3770);

    }

}
