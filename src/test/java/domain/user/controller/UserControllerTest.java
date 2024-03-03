package domain.user.controller;

import static org.assertj.core.api.Assertions.assertThat;

import domain.user.model.User;
import domain.user.repository.UserRepository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpStatus;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;

class UserControllerTest {

    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);
    private final UserController userController = new UserController();
    private ByteArrayOutputStream out;
    private DataOutputStream dos;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        dos = new DataOutputStream(out);
        UserRepository.save(User.of("test", "password", "name", "email"));
    }

    @AfterEach
    void tearDown() {
        UserRepository.deleteAll();
    }

    @Test
    void signup() throws IOException {
        // given
        String body = "userId=test&password=password&name=name&email=email";
        InputStream in = new ByteArrayInputStream(String.format("""
            POST /user/create HTTP/1.1
            Host: localhost:8080
            Connection: keep-alive
            Content-Length: %d
            Content-Type: application/x-www-form-urlencoded
            Accept: */*
                        
            %s
            """, body.length(), body).getBytes());

        //when
        userController.controll(HttpRequest.from(in), HttpResponse.of(dos));
        HttpResponse response = HttpResponse.from(out);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(
            response.getHeader("Location").orElseThrow())
            .isEqualTo("/index.html");
    }

    @Test
    void login() throws IOException {
        // given
        String body = "userId=test&password=password";
        InputStream in = new ByteArrayInputStream(String.format("""
            POST /user/login HTTP/1.1
            Host: localhost:8080
            Connection: keep-alive
            Content-Length: %d
            Content-Type: application/x-www-form-urlencoded
            Accept: */*
                        
            %s
            """, body.length(), body).getBytes());

        //when
        userController.controll(HttpRequest.from(in), HttpResponse.of(dos));
        HttpResponse response = HttpResponse.from(out);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(
            response.getHeader("Set-Cookie").orElseThrow())
            .isEqualTo("logined=true");
        assertThat(
            response.getHeader("Location").orElseThrow())
            .isEqualTo("/index.html");
    }

    @Test
    void login_failed() throws IOException {
        // given
        String body = "userId=test&password=wrong-password";
        InputStream in = new ByteArrayInputStream(String.format("""
            POST /user/login HTTP/1.1
            Host: localhost:8080
            Connection: keep-alive
            Content-Length: %d
            Content-Type: application/x-www-form-urlencoded
            Accept: */*
                        
            %s
            """, body.length(), body).getBytes());

        //when
        userController.controll(HttpRequest.from(in), HttpResponse.of(dos));
        HttpResponse response = HttpResponse.from(out);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(
            response.getHeader("Set-Cookie").orElseThrow())
            .isEqualTo("logined=false");
        assertThat(
            response.getHeader("Location").orElseThrow())
            .isEqualTo("/user/login_failed.html");
    }

    @Test
    void getUserList() throws IOException {
        // given
        InputStream in = new ByteArrayInputStream("""
            GET /user/list HTTP/1.1
            Host: localhost:8080
            Connection: keep-alive
            Accept: */*
            Cookie: logined=true
                        
            """.getBytes());

        //when
        userController.controll(HttpRequest.from(in), HttpResponse.of(dos));
        HttpResponse response = HttpResponse.from(out);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(UserRepository.findAll().toString().getBytes());
    }

    @Test
    void getUserList_fail() throws IOException {
        // given
        InputStream in = new ByteArrayInputStream("""
            GET /user/list HTTP/1.1
            Host: localhost:8080
            Connection: keep-alive
            Accept: */*
                        
            """.getBytes());

        //when
        userController.controll(HttpRequest.from(in), HttpResponse.of(dos));
        HttpResponse response = HttpResponse.from(out);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(
            response.getHeader("Location").orElseThrow())
            .isEqualTo("/user/login.html");
    }
}