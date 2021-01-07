package teclan.netty;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import teclan.netty.client.FileClient;

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
