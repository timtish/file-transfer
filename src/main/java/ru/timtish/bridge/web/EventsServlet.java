package ru.timtish.bridge.web;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket connection.
 */
@ServerEndpoint("/events")
public class EventsServlet {

    private static List<RemoteEndpoint> sessions = new ArrayList<RemoteEndpoint>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("open comet " + session.getId());
        sessions.add(session.getBasicRemote());
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("close comet " + session.getId());
        sessions.remove(session.getBasicRemote());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(session.getId() + ": " + message);

        for (RemoteEndpoint s : sessions) {
            try {
                ((RemoteEndpoint.Basic)s).sendText(session.getId() + ": " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        System.out.println("error comet " + session.getId() + ": " + e.getLocalizedMessage());
    }
}
