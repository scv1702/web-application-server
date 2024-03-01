package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.user.controller.UserController;

public class RequestHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;
    
    private static final Map<String, Controller> controllers = new HashMap<>();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        controllers.put("/user", new UserController());
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest httpRequest = HttpRequest.from(in);

            HttpResponse response = null;

            if (httpRequest.isStaticFileRequest()) {
                File file = new File("./webapp" + httpRequest.getUri().getUri());
                log.debug(file.getAbsolutePath());
                response = new HttpResponse(HttpStatus.OK, Files.readAllBytes(file.toPath()));
            } else {
                String requestPath = httpRequest.getUri().getRequestPath();
                for (String key: controllers.keySet()) {
                    if (requestPath.contains(key)) {
                        response = controllers.get(key).controll(httpRequest);
                        break;
                    }
                }
            }

            response(dos, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response(DataOutputStream dos, HttpResponse response) {
        byte[] body = response.getBody();
        HttpStatus status = response.getStatus();
        responseHeader(dos, status, body.length);
        responseBody(dos, body);
    }

    private void responseHeader(DataOutputStream dos, HttpStatus status, int lengthOfBodyContent) {
        try {
            dos.writeBytes(String.format("HTTP/1.1 %d %s \r\n", status.code(), status.value()));
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
