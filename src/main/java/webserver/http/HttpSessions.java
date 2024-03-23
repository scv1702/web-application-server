package webserver.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpSessions {
    private static final Map<String, HttpSession> sessions = new HashMap<>();

    public static Optional<HttpSession> getSession(Optional<String> sessionId) {
        return sessionId.map(HttpSessions.sessions::get);
    }

    public static HttpSession createSession() {
        HttpSession session = HttpSession.createSession();
        sessions.put(session.getId(), session);
        return session;
    }

    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public static void clear() {
        sessions.clear();
    }
}
