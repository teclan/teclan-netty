package teclan.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.cache.CounterCache;
import teclan.netty.cache.FileInfoCache;
import teclan.netty.model.FileInfo;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileClientHandler extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileClientHandler.class);
    private ChannelHandlerContext ctx;
    private static ExecutorService EXCUTORS = Executors.newFixedThreadPool(1);
    private Timer timer = new Timer();
    private Thread fileReceiveServer;
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

    public void setParamFetcher(ParamFetcher paramFetcher) {
        this.paramFetcher = paramFetcher;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        this.ctx = ctx;
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

    public void run() {
        heartbeatStart();
        fileReceiveStart();

        if (monitor == null) {
            monitor = new DefaultMonitor();
        }
        if (paramFetcher == null) {
            paramFetcher = new DefaultParamFetcher();
        }
    }

    private void fileReceiveStart(){
        fileReceiveServer = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        final FileInfo fileInfo = FileInfoCache.take();
                        FileInfoHandler.write(fileInfo);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        });
        fileReceiveServer.start();
        LOGGER.info("文件下载服务已启动...");
    }

    /**
     * 启动发送心跳数据，数据包为空
     */
    public void heartbeatStart(){
        LOGGER.info("心跳程序已启动 ....");
        final FileInfo fileInfo = new FileInfo();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    FileInfoHandler.send(ctx,fileInfo);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(),e);
                }
            }
        },10000,10000);
    }

    public void stop() {
        timer.cancel();
        fileReceiveServer.stop();
        ctx.close();
    }

    public void upload(final String srcDir, final String dstDir, final String fileName) throws Exception {
        EXCUTORS.submit(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                FileInfoHandler.transfer(EXCUTORS,monitor,paramFetcher, ctx,srcDir,dstDir,fileName);
                return true;
            }
        });
    }
}
