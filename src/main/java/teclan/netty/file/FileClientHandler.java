package teclan.netty.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class FileClientHandler extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileClientHandler.class);

    private FileOutputStream ofs;

    RandomAccessFile randomFile;

    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        String path = "/home/dev/Desktop/m2.zip";

        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();

        }

        ByteBuf buf = (ByteBuf) msg;

        if (buf.isReadable()) {

            randomFile = new RandomAccessFile(path, "rw");
            long fileLength = randomFile.length();

            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);

            randomFile.seek(fileLength);
            randomFile.write(bytes);
            randomFile.close();

            buf.release();

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
