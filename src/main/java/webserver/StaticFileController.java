package webserver;

import java.io.File;
import java.io.IOException;
import webserver.http.response.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.request.HttpRequest;

public class StaticFileController {

    public static void controll(HttpRequest request, HttpResponse response) throws IOException {
        File file = new File("webapp" + request.getPath());
        if (!file.exists()) {
            response.status(HttpStatus.NOT_FOUND).send();
        }
        response.status(HttpStatus.OK)
            .body(file)
            .send();
    }
}
