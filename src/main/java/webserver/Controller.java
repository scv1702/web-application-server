package webserver;

import java.io.IOException;
import webserver.http.response.HttpResponse;
import webserver.http.request.HttpRequest;

public interface Controller {
    void controll(HttpRequest request, HttpResponse response) throws IOException;
}