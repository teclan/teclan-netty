package teclan.netty.handler;

import java.util.Map;

public interface Monitor {

    public String getProcess(String file);

    public void serProcess(String filePath,long max,long value);

    public Map<String,String> getCahche();

    public void remove(String filePath);
}
