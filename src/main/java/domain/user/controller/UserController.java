package domain.user.controller;

import domain.user.model.User;
import domain.user.service.UserService;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.Controller;
import webserver.http.HttpStatus;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;

public class UserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public void controll(HttpRequest request, HttpResponse response) throws IOException {
        String requestPath = request.getPath();
        if (requestPath.equals("/user/create") && request.getMethod().isPost()) {
            signup(request, response);
        }
        if (requestPath.equals("/user/login") && request.getMethod().isPost()) {
            login(request, response);
        }
        if (requestPath.equals("/user/list") && request.getMethod().isGet()) {
            getUserList(request, response);
        }
    }

    private void signup(HttpRequest request, HttpResponse response) throws IOException {
        try {
            String userId = request.getBody().getParameter("userId");
            String password = request.getBody().getParameter("password");
            String name = request.getBody().getParameter("name");
            String email = request.getBody().getParameter("email");
            User user = UserService.signup(userId, password, name, email);
            response.status(HttpStatus.FOUND)
                .body(user.toString())
                .header("Location", "/index.html")
                .send();
        } catch (IllegalArgumentException e) {
            response.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage())
                .send();
        }
    }

    private void login(HttpRequest request, HttpResponse response) throws IOException {
        try {
            String userId = request.getBody().getParameter("userId");
            String password = request.getBody().getParameter("password");
            if (UserService.login(userId, password)) {
                response.status(HttpStatus.FOUND)
                    .header("Location", "/index.html")
                    .header("Set-Cookie", "logined=true")
                    .send();
            }
            response.status(HttpStatus.FOUND)
                .header("Location", "/user/login_failed.html")
                .header("Set-Cookie", "logined=false")
                .send();
        } catch (IllegalArgumentException e) {
            response.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage())
                .send();
        }
    }

    private void getUserList(HttpRequest request, HttpResponse response) throws IOException {
        boolean logined = request.getCookie("logined")
            .map("true"::equals)
            .orElse(false);
        if (logined) {
            response.status(HttpStatus.OK)
                .body(UserService.getUserList().toString())
                .send();
        } else {
            response.status(HttpStatus.FOUND)
                .header("Location", "/user/login.html")
                .send();
        }
    }
}
