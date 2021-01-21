package teclan.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.cache.CounterCache;
import teclan.netty.cache.FileInfoCache;
import teclan.netty.model.FileInfo;
import teclan.netty.model.PackageType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServerHanlder extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServerHanlder.class);
    private static int poolSize = 10;
    private static ExecutorService EXCUTORS = null;
    private static Monitor monitor;
    private static ParamFetcher paramFetcher;
    private static FileInfoHandler fileInfoHandler;

    private static Map<String,ChannelHandlerContext> CLINET_INFOS = new HashMap<String,ChannelHandlerContext>();

    private String getRemote(ChannelHandlerContext ctx){
        String remote = ctx.channel().remoteAddress().toString();
        return remote;
    }
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();

        LOGGER.error("客户端 {} ==> 服务端 {} 登录", ctx.channel().remoteAddress(), ctx.channel().localAddress());
        CLINET_INFOS.put(getRemote(ctx),ctx);

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("客户端 {} ==> 服务端 {} 异常", ctx.channel().remoteAddress(), ctx.channel().localAddress());
        LOGGER.error(cause.getMessage(), cause);
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
                String srcDir = new File(fileInfo.getSrcFileName()).getParent();
                String dstDir = new File(fileInfo.getDstFileName()).getParent();
                String fileName = new File(fileInfo.getSrcFileName()).getName();
                fileInfoHandler.transfer(EXCUTORS,monitor,paramFetcher, ctx,srcDir,dstDir,fileName);
            }else {
                LOGGER.info("收到未知的数据包类型,{}",fileInfo);
            }
        }
    }

    /**
     * 服务端文件推送
     * @param srcDir
     * @param dstDir
     * @param fileName
     * @throws Exception
     */
    public static void push(final String remote,final String srcDir, final String dstDir, final String fileName) throws Exception {
        final  ChannelHandlerContext ctx = CLINET_INFOS.get(remote);
        if(ctx==null){
            LOGGER.info("文件推送失败，未找到客户端[{}]的连接信息 ... ",remote);
            return;
        }
        LOGGER.info("文件推送  ==> {},{} ",remote,dstDir+ File.separator+fileName);

        initIfNeed();

        fileInfoHandler.transfer(EXCUTORS,monitor,paramFetcher, ctx,srcDir,dstDir,fileName);
    }

    public static void run() {

        if (EXCUTORS == null) {
            EXCUTORS = Executors.newFixedThreadPool(poolSize);
        }
        LOGGER.info("处理文件信息任务已启动，线程池大小：{}", poolSize);

        initIfNeed();

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        final FileInfo fileInfo = FileInfoCache.take();
                       EXCUTORS.submit(new Callable<Boolean>() {

                           public Boolean call() throws Exception {
                               fileInfoHandler.write(monitor,fileInfo);
                               return true;
                           }
                       });
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }

        }).start();
    }

    public static Monitor getMonitor() {
        return monitor;
    }

    public static void setMonitor(Monitor monitor) {
        FileServerHanlder.monitor = monitor;
    }

    public static ParamFetcher getParamFetcher() {
        return paramFetcher;
    }

    public static void setParamFetcher(ParamFetcher paramFetcher) {
        FileServerHanlder.paramFetcher = paramFetcher;
    }

    public static FileInfoHandler getFileInfoHandler() {
        return fileInfoHandler;
    }

    public static void setFileInfoHandler(FileInfoHandler fileInfoHandler) {
        FileServerHanlder.fileInfoHandler = fileInfoHandler;
    }

    private static void initIfNeed(){
        if(monitor==null){
            monitor = new DefaultMonitor();
        }

        if(paramFetcher==null){
            paramFetcher = new DefaultParamFetcher();
        }

        if(fileInfoHandler==null){
            fileInfoHandler = new DefaultFileInfoHandler();
        }

    }

    public static Map<String,ChannelHandlerContext> getClinetInfos(){
        return CLINET_INFOS;
    }

    public static ChannelHandlerContext getClinetInfos(String remote){
        return CLINET_INFOS.get(remote);
    }

}
