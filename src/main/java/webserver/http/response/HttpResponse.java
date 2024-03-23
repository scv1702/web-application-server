package webserver.http.response;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpHeaders;
import webserver.http.HttpStatus;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private final DataOutputStream dos;
    private HttpResponseLine line;
    private final HttpHeaders headers;
    private HttpResponseBody body;

    private HttpResponse(HttpStatus status, HttpHeaders headers, HttpResponseBody body) {
        this.line = HttpResponseLine.of(status);
        this.headers = headers;
        this.body = body;
        this.dos = null;
    }

    private HttpResponse(OutputStream out) {
        this.line = HttpResponseLine.of(HttpStatus.OK);
        this.headers = new HttpHeaders();
        this.body = new HttpResponseBody(new byte[0]);
        this.dos = new DataOutputStream(out);
    }

    public static HttpResponse from(ByteArrayOutputStream out) throws IOException {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
        String line = br.readLine();
        if (line == null || line.isEmpty()) {
            throw new IllegalArgumentException("Invalid Http Response");
        }
        int statusCode = Integer.parseInt(line.split(" ")[1]);
        HttpStatus status = HttpStatus.valueOf(statusCode);
        HttpHeaders headers = HttpHeaders.from(br);
        HttpResponseBody body = HttpResponseBody.from(br);
        return new HttpResponse(status, headers, body);
    }

    public static HttpResponse of(OutputStream out) {
        return new HttpResponse(out);
    }

    public HttpResponse status(HttpStatus status) {
        this.line = HttpResponseLine.of(status);
        return this;
    }

    public HttpResponse body(File file) throws IOException {
        this.body = new HttpResponseBody(Files.readAllBytes(file.toPath()));
        header("Content-Type", Files.probeContentType(file.toPath()) + ";charset=utf-8");
        header("Content-Length", String.valueOf(this.body.getBody().length));
        return this;
    }

    public HttpResponse body(String body) {
        this.body = new HttpResponseBody(body.getBytes());
        header("Content-Type", "text/html;charset=utf-8");
        header("Content-Length", String.valueOf(this.body.getBody().length));
        return this;
    }

    public HttpStatus getStatus() {
        return line.getStatus();
    }

    public Optional<String> getHeader(String key) {
        return headers.getHeader(key);
    }

    public HttpResponse header(String key, String value) {
        headers.addHeader(key, value);
        return this;
    }

    public byte[] getBody() {
        return body.getBody();
    }

    public void send() throws IOException {
        dos.writeBytes(line.toString());
        dos.writeBytes(headers.toString());
        dos.write(body.getBody());
        dos.flush();
    }
}
