package teclan.netty.file;

import java.io.File;
import java.io.FileOutputStream;
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

    private FileOutputStream ofs;

    RandomAccessFile randomFile;

    boolean flag = true;

    static String remote = "/home/dev/Test.txt";

    String local = "/home/dev/Desktop/Test.txt";

    public void channelActive(ChannelHandlerContext ctx) {

        ctx.write(Unpooled.copiedBuffer(remote.getBytes()));
        ctx.flush();
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

            // LOGGER.info("From Server:{}", new String(bytes, "UTF-8"));

            randomFile.seek(fileLength);
            randomFile.write(bytes);
            randomFile.close();

            // ofs = new FileOutputStream(file);
            // byte[] bytes = new byte[buf.readableBytes()];
            // buf.readBytes(bytes);
            // buf.release();
            // ofs.write(bytes);
            // ofs.close();
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

}
