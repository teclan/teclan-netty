package teclan.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.cache.CounterCache;
import teclan.netty.cache.FileInfoCache;
import teclan.netty.model.FileInfo;
import teclan.netty.utils.FileUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServerHanlder extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServerHanlder.class);
    private static int poolSize = 10;
    private static ExecutorService EXCUTORS = null;
    private ChannelHandlerContext channelHandlerContext;

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
        channelHandlerContext = ctx;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("客户端 {} ==> 服务端 {}",ctx.channel().remoteAddress(),ctx.channel().localAddress());
        LOGGER.error(cause.getMessage(), cause);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FileInfo)) {
            return;
        } else {
            FileInfo fileInfo = (FileInfo) msg;
            CounterCache.increase(fileInfo);
            FileInfoCache.put(fileInfo);
        }

    }

    public static void run() {

        if (EXCUTORS == null) {
            EXCUTORS = Executors.newFixedThreadPool(poolSize);
        }
        LOGGER.info("处理文件信息任务已启动，线程池大小：{}", poolSize);

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        final FileInfo fileInfo = FileInfoCache.take();

                        if(!CounterCache.hasCache(fileInfo)){
                            FileInfoCache.put(fileInfo);
                            continue;
                        }

                        byte[] data = fileInfo.getData();
                        if (fileInfo.isDir()) {
                            new File(fileInfo.getDstFileName()).mkdirs();
                            CounterCache.remove(fileInfo);
                            LOGGER.info("文件接收完成 {}",fileInfo.getDstFileName());
                            continue;
                        }

                        File tmp = new File(fileInfo.getTmpFileName());
                        int dataLength = data.length;
                        if (dataLength > 0) {
                            RandomAccessFile randomAccessFile = new RandomAccessFile(tmp, "rw");
                            randomAccessFile.seek(fileInfo.getStart());
                            randomAccessFile.write(data);
                            randomAccessFile.close();
                        }

                        if (CounterCache.isDone(fileInfo)) {
                            File dst = new File(fileInfo.getDstFileName());
                            dst.getParentFile().mkdirs();
                            try {
                                FileUtils.rename(tmp,dst);
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                            CounterCache.remove(fileInfo);
                            LOGGER.info("文件接收完成 {}",dst.getAbsolutePath());
                            continue;
                        }

                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }

        }).start();

    }
}
