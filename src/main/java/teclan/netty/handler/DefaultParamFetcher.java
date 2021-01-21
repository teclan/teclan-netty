package teclan.netty.handler;

import com.alibaba.fastjson.JSONObject;
import teclan.netty.config.Config;

public class DefaultParamFetcher implements ParamFetcher {
    public JSONObject get() {
        JSONObject jsonObject = new JSONObject();

        int size = FileServerHanlder.getClinetInfos().size();
        size = size == 0 ? 1 : size;
        jsonObject.put("slice", Config.SLICE / size);
        return jsonObject;
    }
}
