package webserver.http.response;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpHeaders;
import webserver.http.HttpStatus;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private final HttpResponseLine line;
    private final HttpHeaders headers;
    private final HttpResponseBody body;

    private HttpResponse(HttpStatus status, byte[] body) {
        this.line = HttpResponseLine.of(status);
        this.headers = new HttpHeaders();
        this.body = new HttpResponseBody(body);
        if (body.length > 0) {
            addHeader("Content-Length", String.valueOf(body.length));
        }
    }

    public static HttpResponse of(HttpStatus status, String body) {
        return new HttpResponse(status, body.getBytes());
    }

    public static HttpResponse of(HttpStatus status, File file) throws IOException {
        return new HttpResponse(status, Files.readAllBytes(file.toPath()))
            .addHeader("Content-Type", Files.probeContentType(file.toPath()) + ";charset=utf-8");
    }

    public static HttpResponse of(HttpStatus status) {
        return new HttpResponse(status, new byte[0]);
    }

    public HttpStatus getStatus() {
        return line.getStatus();
    }

    public Optional<String> getHeader(String key) {
        return headers.getHeader(key);
    }

    public HttpResponse addHeader(String key, String value) {
        headers.addHeader(key, value);
        return this;
    }

    public byte[] getBody() {
        return body.getBody();
    }

    public void send(DataOutputStream dos) throws IOException {
        dos.writeBytes(line.toString());
        dos.writeBytes(headers.toString());
        dos.write(body.getBody());
        dos.flush();
    }
}
