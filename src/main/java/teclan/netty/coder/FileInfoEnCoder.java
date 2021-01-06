package teclan.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.model.FileInfo;
import teclan.netty.utils.StringUtils;

/**
 * 文件信息编码器
 */
public class FileInfoEnCoder extends MessageToByteEncoder<FileInfo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileInfoEnCoder.class);

    protected synchronized void encode(ChannelHandlerContext channelHandlerContext, FileInfo fileInfo, ByteBuf byteBuf) throws Exception {

        // 文件ID编码
        byte[] data = StringUtils.getBytes(fileInfo.getId());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
        // 源文件路径编码
        data = StringUtils.getBytes(fileInfo.getSrcFileName());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
        // 目标文件路径编码
        data = StringUtils.getBytes(fileInfo.getDstFileName());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
        // 临时文件路径编码
        data = StringUtils.getBytes(fileInfo.getTmpFileName());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);

        // 类型编码
        data = StringUtils.getBytes(fileInfo.getType());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);

        //分片大小编码
        byteBuf.writeInt(fileInfo.getSlice());
        // 所以编码
        byteBuf.writeInt(fileInfo.getIndex());
        // 读取位置编码
        byteBuf.writeLong(fileInfo.getStart());
        // 读取结算位置编码
        byteBuf.writeLong(fileInfo.getPoint());
        // 文件长度编码
        byteBuf.writeLong(fileInfo.getLength());
        // 是否结束编码
        byteBuf.writeBoolean(fileInfo.isDone());
        // 是否目录编码
        byteBuf.writeBoolean(fileInfo.isDir());
        //数据包编码
        if(fileInfo.getData()==null){
            byteBuf.writeInt(0);
        }else {
            byteBuf.writeInt(fileInfo.getData().length);
            byteBuf.writeBytes(fileInfo.getData());
        }
    }
}
