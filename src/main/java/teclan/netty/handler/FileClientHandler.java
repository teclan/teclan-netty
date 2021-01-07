package teclan.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.config.Config;
import teclan.netty.model.FileInfo;
import teclan.netty.utils.IdUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileClientHandler extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileClientHandler.class);
    private ChannelHandlerContext channelHandlerContext;
    private static ExecutorService EXCUTORS = Executors.newFixedThreadPool(1);
    private Monitor monitor;
    private ParamFetcher paramFetcher;

    public FileClientHandler() {
        monitor = new DefaultMonitor();
        paramFetcher = new DefaultParamFetcher();
    }

    public FileClientHandler(Monitor monitor) {
        this.monitor = monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        this.channelHandlerContext = ctx;
    }

    public void clsoe() {
        channelHandlerContext.close();
    }

    public void upload(final String srcDir, final String dstDir, final String fileName) throws Exception {

        if (monitor == null) {
            monitor = new DefaultMonitor();
        }

        EXCUTORS.submit(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                File file = new File(srcDir + File.separator + fileName);
                if (!file.exists()) {
                    throw new Exception(String.format("检测到文件不存在，不允许上传，%s", file.getAbsolutePath()));
                }

                FileInfo fileInfo = new FileInfo(file.getAbsolutePath(), dstDir + File.separator + fileName, file.length());
                fileInfo.setId(IdUtils.get());
                fileInfo.setDefTmpFileName();
                if (file.isDirectory()) {
                    fileInfo.setDir(true);
                    channelHandlerContext.writeAndFlush(fileInfo);
                    monitor.serProcess(file.getAbsolutePath(), 100, 100);
                } else {
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));

                    int slice = paramFetcher.get().getInteger("slice");

                    if(file.length()<slice){
                        slice = (int)file.length();
                    }
                    fileInfo.setSlice(slice);
                    fileInfo.setPackages((int)Math.ceil(file.length()*1.0/slice));

                    byte[] cache = new byte[slice];
                    int cacheLength = 0;
                    long start = 0;
                    int index = 0;
                    do {
                        index++;
                        cacheLength = bis.read(cache);

                        if (cacheLength == -1) {

                        } else {

                            byte[] data = null;
                            data = new byte[cacheLength];
                            System.arraycopy(cache, 0, data, 0, cacheLength);
                            fileInfo.setData(data);
                            fileInfo.setStart(start);
                            fileInfo.setIndex(index);
                            fileInfo.setPoint(fileInfo.getPoint() + cacheLength);
                            channelHandlerContext.writeAndFlush(fileInfo);
                            if (index % 100 == 0) {
                                monitor.serProcess(file.getAbsolutePath(), fileInfo.getPackages(), fileInfo.getIndex());
                            }

                            start += cacheLength;
                            Thread.sleep(10);
                        }
                    } while (cacheLength > 0);
                    monitor.serProcess(file.getAbsolutePath(), 100, 100);
                    LOGGER.info("文件传输完成：{}", file.getAbsolutePath());
                }
                return true;
            }
        });


    }
}
