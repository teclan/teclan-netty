package teclan.netty;

import org.junit.Test;
import teclan.netty.service.FileServer;

public class FileServerTest {

    @Test
    public void setup() {
        FileServer fileServer = new FileServer();
        fileServer.run(7070);
    }  
}
