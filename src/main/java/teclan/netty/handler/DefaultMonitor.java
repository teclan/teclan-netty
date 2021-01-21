package teclan.netty.handler;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class DefaultMonitor implements Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMonitor.class);
    private static final DecimalFormat DF = new DecimalFormat("###.00");

    private static Map<String,String> PUSH_PROCESS = new HashMap<String, String>();
    private static Map<String,String> RECE_PROCESS = new HashMap<String, String>();

    public String getPushProcess(String filePath) {
        String process = PUSH_PROCESS.get(filePath);
        LOGGER.info("当前推送进度：{} {}%",filePath,process);
        return process;
    }


    public String getReceProcess(String filePath) {
        String process = RECE_PROCESS.get(filePath);
        LOGGER.info("当前接收进度：{} {}%",filePath,process);
        return process;
    }

    public void setPushProcess(String filePath, long max, long value) {
        double v = value*100.0/max;
        v=v>100.0?100.0:v;
        String process = DF.format(v);
        LOGGER.info("当前推送进度：{} {}%",filePath,process);
        PUSH_PROCESS.put(filePath,process);
    }

    public void setReceProcess(String filePath, long max, long value) {
        double v = value*100.0/max;
        v=v>100.0?100.0:v;
        String process = DF.format(v);
        LOGGER.info("当前接收进度：{} {}%",filePath,process);
        RECE_PROCESS.put(filePath,process);
    }

    public Map<String, String> getPushCahche(String filePath) {
        return PUSH_PROCESS;
    }

    public Map<String, String> getReceCahche(String filePath) {
        return RECE_PROCESS;
    }

    public void removePushCahche(String filePath) {
        PUSH_PROCESS.remove(filePath);
    }

    public void removeReceCahche(String filePath) {
        RECE_PROCESS.remove(filePath);
    }


}
