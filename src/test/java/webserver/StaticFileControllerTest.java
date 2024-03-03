package webserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;

class StaticFileControllerTest {

    private ByteArrayOutputStream out;
    private DataOutputStream dos;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        dos = new DataOutputStream(out);
    }

    @Test
    public void acceptHtml() throws IOException {
        // given
        String method = "GET";
        String requestPath = "/user/form.html";
        String queryString = "";
        String uri = requestPath + queryString;
        String host = "localhost:8080";
        InputStream in = new ByteArrayInputStream(String.format("""
                %s %s HTTP/1.1
                Host: %s
                Connection: keep-alive
                Accept: text/html,*/*;q=0.1
                        
                """,
            method, uri, host).getBytes());

        // when
        StaticFileController.controll(HttpRequest.from(in), HttpResponse.of(dos));
        HttpResponse response = HttpResponse.from(out);

        // then
        assertThat(response.getHeader("Content-Type").orElseThrow()).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    public void acceptCss() throws IOException {
        // given
        String method = "GET";
        String requestPath = "/css/styles.css";
        String queryString = "";
        String uri = requestPath + queryString;
        String host = "localhost:8080";
        InputStream in = new ByteArrayInputStream(String.format("""
                %s %s HTTP/1.1
                Host: %s
                Connection: keep-alive
                Accept: text/css,*/*;q=0.1
                        
                """,
            method, uri, host).getBytes());

        // when
        StaticFileController.controll(HttpRequest.from(in), HttpResponse.of(dos));
        HttpResponse response = HttpResponse.from(out);

        // then
        assertThat(response.getHeader("Content-Type").orElseThrow()).isEqualTo("text/css;charset=utf-8");
    }
}