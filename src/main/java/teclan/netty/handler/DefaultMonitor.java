package teclan.netty.handler;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class DefaultMonitor implements Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMonitor.class);
    private static final DecimalFormat DF = new DecimalFormat("#.00");

    private static Map<String,String> PROCESS = new HashMap<String, String>();

    public String getProcess(String filePath) {
        String process = PROCESS.get(filePath);
        LOGGER.info("当前进度：{} {}%",filePath,process);
        return process;
    }

    public void serProcess(String filePath, long max, long value) {
        double v = value*100.0/max;
        v=v>100.0?100.0:v;
        String process = DF.format(v);
        LOGGER.info("当前进度：{} {}%",filePath,process);
        PROCESS.put(filePath,process);
    }

    public Map<String, String> getCahche() {
        return PROCESS;
    }

    public void remove(String filePath) {
        PROCESS.remove(filePath);
    }
}
