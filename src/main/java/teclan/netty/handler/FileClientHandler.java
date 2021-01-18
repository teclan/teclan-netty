package teclan.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.cache.CounterCache;
import teclan.netty.cache.FileInfoCache;
import teclan.netty.model.FileInfo;
import teclan.netty.model.PackageType;

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
    private static FileInfoHandler fileInfoHandler;

    public FileClientHandler() {
        monitor = new DefaultMonitor();
        paramFetcher = new DefaultParamFetcher();
        fileInfoHandler = new DefaultFileInfoHandler();
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
    public void setFileInfoHandler(FileInfoHandler fileInfoHandler) {
        this.fileInfoHandler = fileInfoHandler;
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

            if(PackageType.HEARBEAT.compareTo(fileInfo.getPackageType())==0){ // 客户端发送的心跳数据包
                LOGGER.info("收到心跳包,{}",fileInfo);
            }else if(PackageType.DATA.compareTo(fileInfo.getPackageType())==0){// 客户端发送的文件数据包
                CounterCache.increase(fileInfo);
                FileInfoCache.put(fileInfo);
            }else if(PackageType.CMD_NEED_REPEAT.compareTo(fileInfo.getPackageType())==0){// 客户端请求重复推送文件,当文件解析异常时发送
                // TODO
            }else {
                LOGGER.info("收到未知的数据包类型,{}",fileInfo);
            }
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
                        fileInfoHandler.write(fileInfo);
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
        fileInfo.setPackageType(PackageType.HEARBEAT);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    fileInfoHandler.send(ctx,fileInfo);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(),e);
                }
            }
        },10000,1000000);
    }

    public void stop() {
        timer.cancel();
        fileReceiveServer.stop();
        ctx.close();
    }

    public void upload(final String srcDir, final String dstDir, final String fileName) throws Exception {
        EXCUTORS.submit(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                fileInfoHandler.transfer(EXCUTORS,monitor,paramFetcher, ctx,srcDir,dstDir,fileName);
                return true;
            }
        });
    }

    public ChannelHandlerContext getCtx(){
        return ctx;
    }
}
