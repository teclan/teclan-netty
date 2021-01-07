package teclan.netty.handler;

import com.alibaba.fastjson.JSONObject;
import teclan.netty.config.Config;

public class DefaultParamFetcher implements ParamFetcher {
    public JSONObject get() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("slice", Config.SLICE);
        return jsonObject;
    }
}
