package teclan.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import teclan.netty.model.FileInfo;

import java.util.concurrent.ExecutorService;

public interface FileInfoHandler {

    public void write(FileInfo fileInfo) throws Exception;

    public void transfer(final ExecutorService EXCUTORS, final Monitor monitor, final ParamFetcher paramFetcher, final ChannelHandlerContext ctx, final String srcDir, final String dstDir, final String fileName) throws Exception ;

    public void send(ChannelHandlerContext ctx, Object o) throws Exception;

    public void push (FileInfo fileInfo) throws Exception;

    public void writeFail(FileInfo fileInfo) throws Exception;
}
