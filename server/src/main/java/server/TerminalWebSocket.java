package server;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import pty.NonBlockingPty;

@ServerWebSocket("/terminal")
public class TerminalWebSocket {
    private WebSocketBroadcaster broadcaster;

    private Map<String, NonBlockingPty> processes = new ConcurrentHashMap<>();

    public TerminalWebSocket(WebSocketBroadcaster broadcaster) {
      this.broadcaster = broadcaster;
    }

    @OnOpen
    public void onOpen(WebSocketSession session) throws IOException {
        NonBlockingPty process = new NonBlockingPty("bash");
        process.addOutputListener(session.getId(), session::sendSync);
        process.start();
        processes.put(session.getId(), process);
    }

    @OnMessage
    public void onMessage(String message, WebSocketSession session) {
        processes.get(session.getId()).input(message.getBytes(StandardCharsets.UTF_8));
    }

    @OnClose
    public void onClose(WebSocketSession session) {
        processes.get(session.getId()).stop();
    }
}
