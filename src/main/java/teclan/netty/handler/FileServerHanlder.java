package teclan.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.model.FileInfo;
import teclan.netty.utils.FileUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class FileServerHanlder extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServerHanlder.class);

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        LOGGER.info("收到数据,{}", msg);

        if (!(msg instanceof FileInfo)) {
            return;
        }
        FileInfo fileInfo = (FileInfo) msg;

        LOGGER.info("{},第 {} 个数据包已收录 ...", fileInfo.getSrcFileName(), fileInfo.getIndex());

        String id = fileInfo.getId();
        byte[] data = fileInfo.getData(); // 本次传输的数据

        if (fileInfo.isDir()) {
            new File(fileInfo.getDstFileName()).mkdirs();
            return;
        }


        File tmp = new File(fileInfo.getTmpFileName());
        int dataLength = data.length;
        if (dataLength > 0) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(tmp, "rw");
            randomAccessFile.seek(fileInfo.getStart());
            randomAccessFile.write(data);
        } else {
            File dst = new File(fileInfo.getDstFileName());
            dst.getParentFile().mkdirs();
            tmp.renameTo(dst);
        }
    }
}
