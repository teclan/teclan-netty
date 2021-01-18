package teclan.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.netty.model.FileInfo;

public class DefaultFileInfoHandler extends AbstractFileInfoHandler{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFileInfoHandler.class);

    public void push(FileInfo fileInfo) throws Exception {
        LOGGER.warn("文件推送，但未采取任何措施...,{}",fileInfo);
    }

    public void writeFail(FileInfo fileInfo) throws Exception {
        LOGGER.warn("文件接收失败，但未采取任何措施...,{}",fileInfo);
    }
}
