package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Connection {
    String getInetAddress();
    int getPort();
    InputStream getInputStream() throws IOException;
    OutputStream getOutputStream() throws IOException;
}
