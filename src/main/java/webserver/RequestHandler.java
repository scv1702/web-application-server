package webserver;

import domain.user.controller.UserController;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpSession;
import webserver.http.HttpSessions;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;

public class RequestHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final Map<String, Controller> controllers = new HashMap<>();
    private final Connection connection;


    static {
        controllers.put("/user", new UserController());
    }

    public RequestHandler(Connection connection) {
        this.connection = connection;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = HttpRequest.from(in);
            HttpResponse response = HttpResponse.of(out);

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

            if (HttpSessions.getSession(request.getCookie("JSESSIONID")).isEmpty()) {
                response.header("Set-Cookie", "JSESSIONID=" + request.getSession().getId());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
