package webserver;

import domain.user.controller.UserController;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;

public class RequestHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/user", new UserController());
    }

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest request = HttpRequest.from(in);
            HttpResponse response = HttpResponse.of(dos);

            if (request.isStaticFileRequest()) {
                StaticFileController.controll(request, response);
            } else {
                String requestPath = request.getPath();
                for (String key : controllers.keySet()) {
                    if (requestPath.contains(key)) {
                        controllers.get(key).controll(request, response);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
