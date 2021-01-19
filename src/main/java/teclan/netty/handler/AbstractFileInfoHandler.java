package teclan.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.cache.CounterCache;
import teclan.netty.cache.FileInfoCache;
import teclan.netty.model.FileInfo;
import teclan.netty.model.PackageType;
import teclan.netty.utils.FileUtils;
import teclan.netty.utils.IdUtils;

import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public abstract class AbstractFileInfoHandler implements FileInfoHandler{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFileInfoHandler.class);

    public void write(FileInfo fileInfo) throws Exception {

        if (!CounterCache.hasCache(fileInfo)) {
            FileInfoCache.put(fileInfo);
            return;
        }

        byte[] data = fileInfo.getData();
        if (fileInfo.isDir()) {
            new File(fileInfo.getDstFileName()).mkdirs();
            CounterCache.remove(fileInfo);
            LOGGER.info("文件接收完成 {}", fileInfo.getDstFileName());
            return;
        }

        File tmp = new File(fileInfo.getTmpFileName());
        int dataLength = data.length;
        if (dataLength > 0) {
            RandomAccessFile randomAccessFile = null;
            try {
                randomAccessFile = new RandomAccessFile(tmp, "rw");
                randomAccessFile.seek(fileInfo.getStart());
                randomAccessFile.write(data);
            } catch (Exception e) {
                LOGGER.debug(e.getMessage(), e);
                FileInfoCache.put(fileInfo);
                return;
            } finally {
                randomAccessFile.close();
            }

        }

        if (CounterCache.isDone(fileInfo)) {
            File dst = new File(fileInfo.getDstFileName());
            try {
                FileUtils.rename(tmp, dst);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            CounterCache.remove(fileInfo);
            LOGGER.info("文件接收完成 {} ", dst.getAbsolutePath());
            String md5 =FileUtils.getFileSummary(dst,"MD5");
            if(!fileInfo.getMd5().equals(md5)){
                LOGGER.info("文件接收完成 {},但文件内容有丢失 ... ", dst.getAbsolutePath());
                writeFail(fileInfo);
            }else{
                LOGGER.info("文件接收完成 {} ", dst.getAbsolutePath());
            }
            return;
        }
    }

    public void transfer(final ExecutorService EXCUTORS, final Monitor monitor, final ParamFetcher paramFetcher, final ChannelHandlerContext ctx, final String srcDir, final String dstDir, final String fileName) throws Exception {

        if (monitor == null) {
            throw new Exception("参数 monitor 未配置...");
        }

        if (paramFetcher == null) {
            throw new Exception("参数 paramFetcher 未配置...");
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
                fileInfo.setMd5(FileUtils.getFileSummary(file,"MD5"));
                fileInfo.setPackageType(PackageType.DATA);
                fileInfo.setRouter(getRemote(ctx));

                push(getRemote(ctx),fileInfo);

                if (file.isDirectory()) {
                    fileInfo.setDir(true);
                    send(ctx, fileInfo);
                    monitor.serProcess(file.getAbsolutePath(), 100, 100);
                } else {
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                    int slice = paramFetcher.get().getInteger("slice");
                    if (file.length() < slice) {
                        slice = (int) file.length();
                    }
                    fileInfo.setSlice(slice);
                    fileInfo.setPackages((int) Math.ceil(file.length() * 1.0 / slice));

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
                            send(ctx, fileInfo);
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

    public void send(ChannelHandlerContext ctx, Object o) throws Exception {
        if (ctx == null) {
            throw new Exception(String.format("检测到连接未初始化成功，发送失败，内容：%s", o));
        }
        ctx.writeAndFlush(o);
    }

    public void writeFail(FileInfo fileInfo) throws Exception {
        LOGGER.info("接收文件失败,{}",fileInfo);
    }

    private String getRemote(ChannelHandlerContext ctx){
        String remote = ctx.channel().remoteAddress().toString();
        return remote;
    }
}

