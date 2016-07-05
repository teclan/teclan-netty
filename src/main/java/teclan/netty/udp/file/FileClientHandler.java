package teclan.netty.udp.file;

import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class FileClientHandler
        extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileClientHandler.class);

    RandomAccessFile randomFile;

    String local = "/home/dev/Desktop/1.iso";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer("/home/dev/1.iso", CharsetUtil.UTF_8),
                FileClient.getInetSocketAddress()));

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx,
            DatagramPacket msg) {
        // TODO Auto-generated method stub
        String req = msg.content().toString(CharsetUtil.UTF_8);

        LOGGER.info("From Server:{}", req);

        ByteBuf buf = (ByteBuf) msg.content();

        if (buf.isReadable()) {

            try {
                randomFile = new RandomAccessFile(local, "rw");

                long fileLength = randomFile.length();

                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);

                randomFile.seek(fileLength);
                randomFile.write(bytes);
                randomFile.close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

        }
        // 此处不需要 buf.release()，否则报以下异常
        // io.netty.util.IllegalReferenceCountException: refCnt: 0, decrement: 1
        // buf.release();
        ctx.flush();

    }

}
