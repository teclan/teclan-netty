package teclan.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.cache.FileInfoCache;
import teclan.netty.model.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServerHanlder extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServerHanlder.class);
    private int poolSize = 10;
    private ExecutorService EXCUTORS = null;

    public FileServerHanlder() {
    }

    public FileServerHanlder(int poolSize) {
        this.poolSize = poolSize;
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        LOGGER.info("收到数据,{}", msg);

        if (!(msg instanceof FileInfo)) {
            return;
        } else {
            FileInfo fileInfo = (FileInfo) msg;
            FileInfoCache.put(fileInfo);
        }

    }

    public void run() {

        if (EXCUTORS == null) {
            EXCUTORS = Executors.newFixedThreadPool(poolSize);
        }
        LOGGER.info("处理文件信息任务已启动，线程池大小：{}", poolSize);


        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        final FileInfo fileInfo = FileInfoCache.take();
                        byte[] data = fileInfo.getData();
                        if (fileInfo.isDir()) {
                            new File(fileInfo.getDstFileName()).mkdirs();
                            continue;
                        }

                        File tmp = new File(fileInfo.getTmpFileName());
                        if (fileInfo.isDone()) {
                            File dst = new File(fileInfo.getDstFileName());
                            dst.getParentFile().mkdirs();
                            try {
                                tmp.renameTo(dst);
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                            continue;
                        }

                        int dataLength = data.length;
                        if (dataLength > 0) {
                            RandomAccessFile randomAccessFile = new RandomAccessFile(tmp, "rw");
                            randomAccessFile.seek(fileInfo.getStart());
                            randomAccessFile.write(data);
                            randomAccessFile.close();
                        } else {
                            File dst = new File(fileInfo.getDstFileName());
                            dst.getParentFile().mkdirs();
                            try {
                                tmp.renameTo(dst);
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }

                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }

        }).start();

    }
}
