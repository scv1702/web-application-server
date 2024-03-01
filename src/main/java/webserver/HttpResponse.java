package webserver;

import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private Uri uri;
    private HttpStatus status;
    private String headers;
    private byte[] body;
    
    public HttpResponse(HttpStatus status, String body) {
        this.status = status;
        this.body = body.getBytes();
    }

    public HttpResponse(HttpStatus status, byte[] body) {
        this.status = status;
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
