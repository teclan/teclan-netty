package teclan.netty.coding;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.model.FileInfo;

import java.util.List;

public class FileInfoDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileInfoDecoder.class);

    protected synchronized void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        byteBuf.markReaderIndex();

        if(byteBuf.toString().equals("EmptyByteBufBE")){
            return;
        }

        int length = 0;
        FileInfo fileInfo = new FileInfo();
        try {

            length = byteBuf.readInt();
            if (waitCache(length,byteBuf)) {
                byteBuf.resetReaderIndex();
                return;
            }
            byte[] data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setId(new String(data,"UTF-8"));

            length = byteBuf.readInt();
            if (waitCache(length,byteBuf)) {
                byteBuf.resetReaderIndex();
                return;
            }
            data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setSrcFileName(new String(data,"UTF-8"));

            length = byteBuf.readInt();
            if (waitCache(length,byteBuf)) {
                byteBuf.resetReaderIndex();
                return;
            }
            data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setDstFileName(new String(data,"UTF-8"));

            length = byteBuf.readInt();
            if (waitCache(length,byteBuf)) {
                byteBuf.resetReaderIndex();
                return;
            }
            data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setTmpFileName(new String(data,"UTF-8"));

            fileInfo.setIndex(byteBuf.readInt());
            fileInfo.setStart(byteBuf.readLong());
            fileInfo.setPoint(byteBuf.readLong());
            fileInfo.setLength(byteBuf.readLong());
            fileInfo.setDone(byteBuf.readBoolean());
            fileInfo.setDir(byteBuf.readBoolean());

            length = byteBuf.readInt();

            if (length == 0) {
                fileInfo.setData(null);
            } else {
                if (waitCache(length,byteBuf)) {
                    byteBuf.resetReaderIndex();
                    return;
                }
                data = new byte[length];
                byteBuf.readBytes(data);
                fileInfo.setData(data);
            }
            list.add(fileInfo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private boolean waitCache(int length, ByteBuf byteBuf) {

        int count = byteBuf.readableBytes();
        if (byteBuf.readableBytes() < length) {
            LOGGER.debug("缓冲区可读数据小于期望,可读长度：{}，期望：{}", count, length);
            return true;
        }
        return false;
    }
}
