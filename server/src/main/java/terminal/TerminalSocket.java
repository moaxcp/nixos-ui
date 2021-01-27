package terminal;

import io.micronaut.websocket.WebSocketSession;
import process.NonBlockingProcess;

public class TerminalSocket {
  private WebSocketSession session;
  private NonBlockingProcess process;
}
