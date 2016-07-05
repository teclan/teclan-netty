package teclan.netty.timeback;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class BackServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(BackServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        byteBuf.release();
        LOGGER.info("From Client:{}", new String(req, "UTF-8"));

        String hello = "Hello Client,I'm Server.The time is "
                + new Date().toString();

        ctx.write(Unpooled.copiedBuffer(hello.getBytes()));
        ctx.flush();

    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {

        ByteBuf resp;

        String hello = "Hello Client,I'm Server!Can I help you?";

        resp = Unpooled.copiedBuffer(hello.getBytes());

        // ctx.write(resp);
        // ctx.flush();
        ctx.writeAndFlush(resp);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

}
