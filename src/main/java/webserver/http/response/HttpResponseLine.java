package webserver.http.response;

import webserver.http.HttpStatus;

public class HttpResponseLine {

    private final HttpStatus status;

    private HttpResponseLine(HttpStatus status) {
        this.status = status;
    }

    public static HttpResponseLine of(HttpStatus status) {
        return new HttpResponseLine(status);
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "HTTP/1.1 " + status.code() + " " + status.value() + "\r\n";
    }
}
