package teclan.netty.handler;

import java.util.Map;

public interface Monitor {

    public String getPushProcess(String file);

    public String getReceProcess(String file);

    /**
     * 推送文件进度
     * @param filePath
     * @param max
     * @param value
     */
    public void setPushProcess(String filePath,long max,long value);

    /**
     * 接收文件进度
     * @param filePath
     * @param max
     * @param value
     */
    public void setReceProcess(String filePath,long max,long value);

    public Map<String,String> getPushCahche(String filePath);

    public Map<String,String> getReceCahche(String filePath);

    public void removePushCahche(String filePath);

    public void removeReceCahche(String filePath);
}
