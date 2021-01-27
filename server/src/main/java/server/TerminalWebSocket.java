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
import process.NonBlockingProcess;

@ServerWebSocket("/terminal")
public class TerminalWebSocket {
    private WebSocketBroadcaster broadcaster;

    private Map<String, NonBlockingProcess> processes = new ConcurrentHashMap<>();

    public TerminalWebSocket(WebSocketBroadcaster broadcaster) {
      this.broadcaster = broadcaster;
    }

    @OnOpen
    public void onOpen(WebSocketSession session) throws IOException {
        NonBlockingProcess bash = new NonBlockingProcess(true, "bash");
        bash.start();
        processes.put(session.getId(), bash);
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
