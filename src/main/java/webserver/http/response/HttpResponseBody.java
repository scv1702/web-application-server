package webserver.http.response;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpResponseBody {

    private final byte[] body;

    public HttpResponseBody(byte[] body) {
        this.body = body;
    }

    public static HttpResponseBody from(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line != null && !line.isEmpty()) {
            return new HttpResponseBody(line.getBytes());
        }
        return new HttpResponseBody(new byte[0]);
    }

    @Override
    public String toString() {
        return new String(body);
    }

    public byte[] getBody() {
        return body;
    }
}
