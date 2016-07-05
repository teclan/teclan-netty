package teclan.netty.udp.file;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class FileServerHandler
        extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileServerHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx,
            DatagramPacket msg) throws Exception {

        String req = msg.content().toString(CharsetUtil.UTF_8);

        LOGGER.info("From Client:{}", req);

        sendFile(ctx, msg, req);

    }

    private void sendFile(ChannelHandlerContext ctx, DatagramPacket msg,
            String filePath) {
        try {

            if (!new File(filePath).isFile() || !new File(filePath).exists()) {
                LOGGER.warn("\n{} is not a file!");
                return;
            }

            FileInputStream in = new FileInputStream(filePath);
            long length = new File(filePath).length();

            long sentBytes = 0;

            byte[] buffer = new byte[10240];

            while (in.available() > 0 && sentBytes < length) {
                byte[] data = null;

                if ((length - sentBytes) >= 10240) {
                    in.read(buffer);
                    data = buffer;
                } else {
                    int len = in.read(buffer, 0, (int) (length - sentBytes));
                    data = Arrays.copyOfRange(buffer, 0, len);
                }

                // Unpooled.copiedBuffer(m.toJson(), CharsetUtil.UTF_8);
                ctx.writeAndFlush(new DatagramPacket(
                        Unpooled.copiedBuffer(data), msg.sender()));

                sentBytes += data.length;

                Thread.sleep(10);
            }

            in.close();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        LOGGER.info("The file:{} sent!", filePath);

    }
}
