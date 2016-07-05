package teclan.netty.file;

import java.io.File;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

public class FileServerHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileServerHandler.class);

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {

        String path = "/home/dev/m2.zip";

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(path, "r");
            long length = raf.length();
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, length));
            ctx.flush();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        LOGGER.info("发送完毕");

    }

    public void messageReceived(ChannelHandlerContext ctx, String msg)
            throws Exception {

        LOGGER.info("From Client:{}", msg);

        File file = new File(msg);

        RandomAccessFile raf = new RandomAccessFile(msg, "r");
        long length = raf.length();

        if (file.exists()) {
            if (!file.isFile()) {
                ctx.write("Not a file: " + file + '\n');
                return;
            }
            ctx.write(file + " " + file.length() + '\n');
            // ctx.sendFile(new DefaultFileRegion(new
            // FileInputStream(file).getChannel(), 0, file.length()));
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, length));
            System.out.println("发送完毕");
        } else {
            ctx.write("File not found: " + file + '\n');
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
