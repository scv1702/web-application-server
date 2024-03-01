package domain.user.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.user.model.User;
import util.HttpRequestUtils;
import webserver.Controller;
import webserver.HttpMethod;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.HttpStatus;

public class UserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public HttpResponse controll(HttpRequest request) throws IOException {
        String requestPath = request.getUri().getRequestPath();
        if (requestPath.equals("/user/create") && request.getMethod() == HttpMethod.POST) {
            return signup(request);
        }
        return null;
    }

    private HttpResponse signup(HttpRequest request) {
        Map<String, String> params = HttpRequestUtils.parseQueryString(request.getBody());
        String userId = params.get("userId");
        String password = params.get("password");
        String name = params.get("name");
        String email = params.get("email");

        User user = User.of(userId, password, name, email);

        log.debug("{} user signup complete", user);

        HttpResponse response = new HttpResponse(HttpStatus.CREATED, user.toString());

        return response;
    }
}
