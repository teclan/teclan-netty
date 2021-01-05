package teclan.netty.coding;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.model.FileInfo;

public class FileInfoEnCoder extends MessageToByteEncoder<FileInfo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileInfoEnCoder.class);

    protected synchronized void encode(ChannelHandlerContext channelHandlerContext, FileInfo fileInfo, ByteBuf byteBuf) throws Exception {

        byte[] data = fileInfo.getId().getBytes();
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);

        data = fileInfo.getSrcFileName().getBytes();
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);

        data = fileInfo.getDstFileName().getBytes();
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);

        byteBuf.writeInt(fileInfo.getIndex());
        byteBuf.writeLong(fileInfo.getStart());
        byteBuf.writeLong(fileInfo.getPoint());
        byteBuf.writeLong(fileInfo.getLength());

        byteBuf.writeBoolean(fileInfo.isDone());
        byteBuf.writeBoolean(fileInfo.isDir());

        byteBuf.writeInt(fileInfo.getData().length);
        byteBuf.writeBytes(fileInfo.getData());
        LOGGER.info("{}",fileInfo);
    }
}
