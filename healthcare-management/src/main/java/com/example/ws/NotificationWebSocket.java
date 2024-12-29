package com.example.ws;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.jboss.logging.Logger;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws/notifications")
@ApplicationScoped
public class NotificationWebSocket {

    private static final Logger LOG = Logger.getLogger(NotificationWebSocket.class);
    private static Set<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        LOG.infof("WebSocket opened: %s", session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        LOG.infof("WebSocket closed: %s", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
        LOG.errorf("WebSocket error on session %s: %s", session.getId(), throwable.getMessage());
    }

    public static void broadcast(String message) {
        sessions.forEach(session -> {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        });
    }
}
