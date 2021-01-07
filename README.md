# 基于 Netty 的文件传输

## 服务端启动示例

```java
public class FileServerTest {

    @Test
    public void setup()  {
        FileServer fileServer = new FileServer();
        fileServer.run(7070);
    }  
}
```

## 客户端启动示例

```java

public class FileClientTest {

    FileClient fileClient = new FileClient("127.0.0.1",7070);

    @Before
    public void setUp(){
        fileClient.start();
    }

    @After
    public void setDown(){
        fileClient.stop();
    }

    @Test
    public void upload() throws Exception {

        for(int i=0;i<5;i++){
            send();
        }
        Thread.sleep(1000*60*20);
    }

    private void send() throws Exception {

        fileClient.upload("E:\\Apps","E:\\remote","ideaIU-2018.3.exe");
        fileClient.upload("E:\\Apps","E:\\remote","ideaIU-2019.3.exe");
        fileClient.upload("E:\\Apps","E:\\remote","ideaIU-2020.1.zip");
        fileClient.upload("E:\\Apps","E:\\remote","INSTALL.txt");
    }
}
```

## 客户端自定义配置设置

```java

public class FileClientSliceTest {



    @Test
    public void clientStart(){
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
```





