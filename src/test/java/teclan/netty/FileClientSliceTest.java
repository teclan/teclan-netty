package teclan.netty;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import teclan.netty.client.FileClient;
import teclan.netty.handler.FileClientHandler;
import teclan.netty.handler.Monitor;
import teclan.netty.handler.ParamFetcher;

import java.util.Map;

public class FileClientSliceTest {



    @Test
    public void clientStart() throws Exception {
        FileClientHandler fileClientHandler = new FileClientHandler();

        /**
         * 设置进度监视器
         */
        fileClientHandler.setMonitor(new Monitor() {
            public String getProcess(String file) {
                // 自定义逻辑
                return null;
            }
            public void serProcess(String filePath, long max, long value) {
                // 自定义逻辑
            }
            public Map<String, String> getCahche() {
                // 自定义逻辑
                return null;
            }

            public void remove(String filePath) {
                // 自定义逻辑
            }
        });

        /**
         * 设置获取参数类，文件传输数据块大小通过 ParamFetcher 设置，参考 FileClientHandler.java
         *                      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
         *                     int slice = paramFetcher.get().getInteger("slice");
         *                     if(file.length()<slice){
         *                         slice = (int)file.length();
         *                     }
         *
         *
         */
        fileClientHandler.setParamFetcher(new ParamFetcher() {
            public JSONObject get() {
                // 自定义逻辑
                return null;
            }
        });

        FileClient fileClient = new FileClient("127.0.0.1",7070);

        fileClient.start();
    }

}
