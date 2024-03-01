package webserver;

import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private final HttpStatus status;
    private String headers;
    private byte[] body = new byte[0];
    
    private HttpResponse(HttpStatus status, String body) {
        this.status = status;
        this.body = body.getBytes();
    }

    private HttpResponse(HttpStatus status, byte[] body) {
        this.status = status;
        this.body = body;
    }

    public static HttpResponse of(HttpStatus status, String body) {
        return new HttpResponse(status, body);
    }

    public static HttpResponse of(HttpStatus status, byte[] body) {
        return new HttpResponse(status, body);
    }

    public byte[] getBody() {
        return body;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}