package teclan.netty.file;

import java.io.File;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;

public class FileServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileServerHandler.class);

    private RandomAccessFile raf;

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {

        // String msg = "The Server is ready!";
        //
        // ctx.write(Unpooled.copiedBuffer(msg.getBytes()));
        // ctx.flush();

    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        LOGGER.info("From Client:{}", new String(bytes, "UTF-8"));

        sendFile(ctx, new String(bytes, "UTF-8"));

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void sendFile(ChannelHandlerContext ctx, String filePath) {
        try {

            if (!new File(filePath).isFile() || !new File(filePath).exists()) {
                LOGGER.warn("\n{} is not a file!");
                return;
            }

            raf = new RandomAccessFile(filePath, "r");
            long length = raf.length();
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, length));
            ctx.flush();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        LOGGER.info("The file:{} sent!", filePath);

    }
}
