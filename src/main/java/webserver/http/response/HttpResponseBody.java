package webserver.http.response;

public class HttpResponseBody {

    private final byte[] body;

    public HttpResponseBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return new String(body);
    }

    public byte[] getBody() {
        return body;
    }
}
