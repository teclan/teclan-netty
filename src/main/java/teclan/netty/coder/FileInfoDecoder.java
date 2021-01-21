package teclan.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.handler.FileServerHanlder;
import teclan.netty.model.FileInfo;
import teclan.netty.model.PackageType;
import teclan.netty.service.FileServer;
import teclan.netty.utils.StringUtils;

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

            PackageType packageType = PackageType.parse(byteBuf.readInt());
            fileInfo.setPackageType(packageType);

            // 文件ID解码
            length = byteBuf.readInt();
            if (waitCache(length,byteBuf)) {
                byteBuf.resetReaderIndex();
                return;
            }
            byte[] data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setId(StringUtils.getString(data));
            // 源文件路径解码
            length = byteBuf.readInt();
            if (waitCache(length,byteBuf)) {
                byteBuf.resetReaderIndex();
                return;
            }
            data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setSrcFileName(StringUtils.getString(data));
            // 目标文件路径解码
            length = byteBuf.readInt();
            if (waitCache(length,byteBuf)) {
                byteBuf.resetReaderIndex();
                return;
            }
            data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setDstFileName(StringUtils.getString(data));
            // 临时文件路径解码
            length = byteBuf.readInt();
            if (waitCache(length,byteBuf)) {
                byteBuf.resetReaderIndex();
                return;
            }
            data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setTmpFileName(StringUtils.getString(data));

            // 路由解码
            length = byteBuf.readInt();
            if (waitCache(length,byteBuf)) {
                byteBuf.resetReaderIndex();
                return;
            }
            data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setRouter(StringUtils.getString(data));

            // 摘要解码
            length = byteBuf.readInt();
            if (waitCache(length,byteBuf)) {
                byteBuf.resetReaderIndex();
                return;
            }
            data = new byte[length];
            byteBuf.readBytes(data);
            fileInfo.setMd5(StringUtils.getString(data));

            // 分片大小解码
            fileInfo.setSlice(byteBuf.readInt());
            // 文件长度解码
            fileInfo.setLength(byteBuf.readLong());
            // 数据包个数解码
            fileInfo.setPackages(byteBuf.readInt());
            // 索引解码
            fileInfo.setIndex(byteBuf.readInt());
            // 读取位置解码
            fileInfo.setStart(byteBuf.readLong());
            // 读取结束位置解码
            fileInfo.setPoint(byteBuf.readLong());

            // 是否完成标志解码
            fileInfo.setDone(byteBuf.readBoolean());
            // 是否目录解码
            fileInfo.setDir(byteBuf.readBoolean());

            // 数据包解码
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
            LOGGER.error("解析数据异常：{}",fileInfo);
            LOGGER.error(e.getMessage(), e);
            byteBuf.resetReaderIndex();
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
