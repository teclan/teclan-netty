package teclan.netty.tcp.timeback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class BackClientHandler extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(BackClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        byteBuf.release();

        LOGGER.info("From Server:{}", new String(req, "UTF-8"));

        Thread.sleep(1000);

        String messageString = "Please send me the time.";
        byte[] b = messageString.getBytes();
        ByteBuf firstMessage = Unpooled.buffer(b.length);
        firstMessage.writeBytes(b);
        ctx.writeAndFlush(firstMessage);

    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
