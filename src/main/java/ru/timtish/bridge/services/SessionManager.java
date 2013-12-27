package ru.timtish.bridge.services;

import org.springframework.stereotype.Service;

import javax.websocket.RemoteEndpoint;
import java.io.IOException;
import java.util.*;

/**
 * Created by ttishin on 25.12.13.
 */
@Service
public class SessionManager {

    /**
     * Map: user id -> remote endpoint
     */
    private static Map<String, Set<RemoteEndpoint>> cometSessions = new HashMap();

    private static Map<String, Set<String>> subscriptions = new HashMap();

    public Set<RemoteEndpoint> getCometSessions() {
        Set<RemoteEndpoint> result = new HashSet<RemoteEndpoint>();
        for (Set<RemoteEndpoint> l : cometSessions.values()) result.addAll(l);
        return result;
    }

    public void addCometSession(String userId, RemoteEndpoint endpoint) {
        Set<RemoteEndpoint> remoteEndpoints = cometSessions.get(userId);
        if (remoteEndpoints == null) remoteEndpoints = new HashSet<RemoteEndpoint>();
        remoteEndpoints.add(endpoint);
        cometSessions.put(userId, remoteEndpoints);
        addSubscription(userId, userId, "root");
    }

    public void addSubscription(String userId, String... subscriptionKeys) {
        for (String subscriptionKey : subscriptionKeys) {
            Set<String> userIds = subscriptions.get(subscriptionKey);
            if (userIds == null) userIds = new HashSet();
            userIds.add(userId);
            subscriptions.put(subscriptionKey, userIds);
        }
    }

    public void send(String subscriptionId, Object o) {
        String msg= o.toString();

        for (String subscriptionKey : subscriptions.keySet()) {
            if (subscriptionKey.startsWith(subscriptionId)) {
                for (String userId : subscriptions.get(subscriptionKey)) {
                    for (RemoteEndpoint endpoint : cometSessions.get(userId)) {
                        try {
                            ((RemoteEndpoint.Basic) endpoint).sendText(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void removeCometSession(RemoteEndpoint remote) {
        for (Map.Entry<String, Set<RemoteEndpoint>> cse : cometSessions.entrySet()) {
            if (cse.getValue().remove(remote) && cse.getValue().isEmpty()) {
                for (Map.Entry<String, Set<String>> se : subscriptions.entrySet()) {
                    se.getValue().remove(cse.getKey());
                }
                cometSessions.remove(cse.getKey());
                return;
            }
        }
    }
}
