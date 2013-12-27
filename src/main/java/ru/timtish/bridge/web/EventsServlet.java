package ru.timtish.bridge.web;

import ru.timtish.bridge.services.SessionManager;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * WebSocket connection.
 */
@ServerEndpoint("/events")
public class EventsServlet {

    // todo: @Autowired
    SessionManager sessions = new SessionManager();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("open comet " + session.getId());
        sessions.addCometSession(getUserId(session), session.getBasicRemote());
    }

    private String getUserId(Session session) {
        return session.getUserPrincipal() != null ? session.getUserPrincipal().getName() :session.getId() ;
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("close comet " + session.getId());
        sessions.removeCometSession(session.getBasicRemote());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(session.getId() + ": " + message);
        if (message.startsWith("Subscribe")) {
            sessions.addSubscription(getUserId(session), message.substring("Subscribe".length(), message.length() - 1));
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        System.out.println("error comet " + session.getId() + ": " + e.getLocalizedMessage());
    }
}
