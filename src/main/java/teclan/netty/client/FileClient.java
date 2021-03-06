package teclan.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.coder.FileInfoDecoder;
import teclan.netty.coder.FileInfoEnCoder;
import teclan.netty.handler.FileClientHandler;

public class FileClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileClient.class);
    private  ChannelFuture channelFuture;
    private FileClientHandler fileClientHandler;
    private String host;
    private int port;


    public FileClient(String host,int port){
        this.fileClientHandler= new FileClientHandler();
        this.host=host;
        this.port=port;
    }

    public FileClient(FileClientHandler fileClientHandler,String host,int port){
        this.fileClientHandler=fileClientHandler;
        this.host=host;
        this.port=port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new FileInfoEnCoder());
                            ch.pipeline().addLast(new FileInfoDecoder());
                            ch.pipeline().addLast(fileClientHandler);
                        }
                    });
            channelFuture = bootstrap.connect(host, port).sync(); //连接服务端
            LOGGER.info("已连接服务器 {}:{}",host,port);
            fileClientHandler.run();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if(e.getMessage().contains("Connection refused")){
                throw new Exception(String.format("连接服务器 %s:%d 失败，请确认文件服务器已启动，并且主机和端口配置正确!",host,port));
            }else {
                throw  e;
            }
        }
    }

    public void stop(){
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }

        fileClientHandler.stop();
    }

    public void upload(String srcDir,String dstDir,String fileName) throws Exception {
        try {
            fileClientHandler.upload(srcDir,dstDir,fileName);
        }catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public FileClientHandler getFileClientHandler(){
        return fileClientHandler;
    }

}
