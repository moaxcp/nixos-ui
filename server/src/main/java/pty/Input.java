package pty;

import com.pty4j.PtyProcess;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Getter;
import lombok.NonNull;

class Input implements Runnable {
  private final PtyProcess process;
  @Getter
  private final ConcurrentLinkedQueue<byte[]> input = new ConcurrentLinkedQueue<>();
  private final OutputStream processInput;
  @Getter
  private volatile boolean running = false;
  @Getter
  private volatile Throwable throwable;

  public Input(@NonNull PtyProcess process, @NonNull OutputStream processInput) {
    this.process = process;
    this.processInput = processInput;
  }

  @Override
  public void run() {
    running = true;
    while (process.isAlive()) {
      byte[] bytes = input.poll();
      if (bytes == null) {
        try {
          Thread.sleep(10);
          continue;
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throwable = e;
          break;
        }
      }
      try {
        processInput.write(bytes);
      } catch (Throwable e) {
        throwable = e;
        break;
      }
    }
    running = false;
  }
}
