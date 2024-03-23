package webserver.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {
    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();

    private HttpSession(String id) {
        this.id = id;
    }

    public static HttpSession createSession() {
        return new HttpSession(createSessionId());
    }

    private static String createSessionId() {
        return UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public void remoteAttribute(String key) {
        attributes.remove(key);
    }

    public void invalidate() {
        attributes.clear();
    }
}
