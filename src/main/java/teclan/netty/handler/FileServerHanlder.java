package teclan.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.cache.CounterCache;
import teclan.netty.cache.FileInfoCache;
import teclan.netty.model.FileInfo;

import java.net.SocketAddress;
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

    private static Map<String,ChannelHandlerContext> CLINET_INFOS = new HashMap<String,ChannelHandlerContext>();

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
        String remote = ctx.channel().remoteAddress().toString();
        LOGGER.error("客户端 {} ==> 服务端 {} 登录", ctx.channel().remoteAddress(), ctx.channel().localAddress());
        CLINET_INFOS.put(remote,ctx);

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
            CounterCache.increase(fileInfo);
            FileInfoCache.put(fileInfo);
        }
    }

    /**
     * 服务端文件推送
     * @param srcDir
     * @param dstDir
     * @param fileName
     * @throws Exception
     */
    public void push(final String remote,final String srcDir, final String dstDir, final String fileName) throws Exception {
        final  ChannelHandlerContext ctx = CLINET_INFOS.get(remote);
        if(ctx==null){
            LOGGER.info("文件推送失败，未找到客户端[{}]的连接信息 ... ",remote);
            return;
        }
        EXCUTORS.submit(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                FileInfoHandler.transfer(EXCUTORS,monitor,paramFetcher, ctx,srcDir,dstDir,fileName);
                return true;
            }
        });
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
                       EXCUTORS.submit(new Callable<Boolean>() {

                           public Boolean call() throws Exception {
                               FileInfoHandler.write(fileInfo);
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
}
