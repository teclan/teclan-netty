package teclan.netty.coding;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.model.FileInfo;
import teclan.netty.service.FileServer;

import java.util.List;

public class FileInfoDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileInfoDecoder.class);

    protected synchronized void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = 0;
        try {
            FileInfo fileInfo = new FileInfo();
            length = byteBuf.readInt();
            byte[] data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setId(new String(data));

            length = byteBuf.readInt();
            data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setSrcFileName(new String(data));

            length = byteBuf.readInt();
            data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setDstFileName(new String(data));

            fileInfo.setIndex(byteBuf.readInt());
            fileInfo.setStart(byteBuf.readLong());
            fileInfo.setPoint(byteBuf.readLong());
            fileInfo.setLength(byteBuf.readLong());
            fileInfo.setDone(byteBuf.readBoolean());
            fileInfo.setDir(byteBuf.readBoolean());

            length = byteBuf.readInt();
            data = new byte[length];
            fileInfo.setData(data);
            LOGGER.info("{}",fileInfo);
            list.add(fileInfo);
        }catch (Exception e){
            LOGGER.info("length = {}",length);
            LOGGER.error(e.getMessage(),e);
        }

    }
}
