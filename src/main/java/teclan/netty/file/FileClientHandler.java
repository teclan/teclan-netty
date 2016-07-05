package teclan.netty.file;

import java.io.File;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class FileClientHandler extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileClientHandler.class);

    RandomAccessFile randomFile;

    private ChannelHandlerContext context = null;

    String local = "/home/dev/Desktop/Test.txt";

    public void channelActive(ChannelHandlerContext ctx) {

        context = ctx;

        // ctx.write(Unpooled.copiedBuffer(remote.getBytes()));
        // ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        File file = new File(local);
        if (!file.exists()) {
            file.createNewFile();

        }

        ByteBuf buf = (ByteBuf) msg;

        if (buf.isReadable()) {

            randomFile = new RandomAccessFile(local, "rw");
            long fileLength = randomFile.length();

            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            buf.release();

            randomFile.seek(fileLength);
            randomFile.write(bytes);
            randomFile.close();

        }
        ctx.flush();

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        if (ctx.channel().isActive()) {
            ctx.writeAndFlush("ERR: " + cause.getClass().getSimpleName() + ": "
                    + cause.getMessage() + '\n')
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void requestFile(String filePath) {

        if (context == null) {
            LOGGER.error("the connect is not init !!!");
            return;
        }
        context.write(Unpooled.copiedBuffer(filePath.getBytes()));
        context.flush();
    }

}
