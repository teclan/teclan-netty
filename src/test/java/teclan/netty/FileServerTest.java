package teclan.netty;

import org.junit.Test;
import teclan.netty.service.FileServer;

import java.io.IOException;

public class FileServerTest {

    @Test
    public void setup() throws InterruptedException, IOException {
        FileServer fileServer = new FileServer();
        fileServer.run(7070);
    }  
}
