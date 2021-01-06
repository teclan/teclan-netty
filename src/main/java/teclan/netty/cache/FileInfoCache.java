package teclan.netty.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.model.FileInfo;

import java.util.concurrent.LinkedBlockingQueue;

public class FileInfoCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileInfoCache.class);
    private static final LinkedBlockingQueue<FileInfo> FILE_INFO_QUEUE = new LinkedBlockingQueue<FileInfo>();

    public static void put(FileInfo fileInfo) throws InterruptedException {
//        LOGGER.info("入队：{}",fileInfo);
        FILE_INFO_QUEUE.put(fileInfo);
    }

    public static FileInfo take() throws InterruptedException {
        FileInfo fileInfo =  FILE_INFO_QUEUE.take();
//        LOGGER.info("出队队：{}",fileInfo);
        return fileInfo;
    }

    public static boolean free() {
        return FILE_INFO_QUEUE.isEmpty();
    }

}
