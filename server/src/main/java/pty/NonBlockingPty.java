package pty;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NonBlockingPty {
  private final PtyProcessBuilder builder;
  boolean redirectError;
  private PtyProcess process;
  private Input input;
  private Output output;
  private Output error;
  private EventLoop eventLoop;
  private final Map<String, Consumer<byte[]>> outputListeners = new HashMap<>();

  public NonBlockingPty(String... command) throws IOException {
    this(true, command);
  }

  public NonBlockingPty(boolean redirectError, String... command) {
    builder = new PtyProcessBuilder(command);
  }

  public void start() throws IOException {
    process = builder.start();
    input = new Input(process, process.getOutputStream());
    output = new Output(process.getInputStream());

    new Thread(input).start();
    new Thread(output).start();
    if(!redirectError) {
      error = new Output(process.getErrorStream());
      new Thread(error).start();
    } else {
      error = null;
    }
    eventLoop = new EventLoop(output, error, process);
    eventLoop.addOutputListeners(outputListeners);
    new Thread(eventLoop).start();
  }

  public void addOutputListener(String name, Consumer<byte[]> listener) {
    if(eventLoop == null) {
      outputListeners.put(name, listener);
    } else {
      eventLoop.addOutputListener(name, listener);
    }
  }

  public void removeOutputListener(String name) {
    eventLoop.removeOutputListener(name);
  }

  public void removeAllOutputListeners() {
    eventLoop.removeAllOutputListeners();
  }

  public void addErrorListener(String name, Consumer<byte[]> listener) {
    eventLoop.addErrorListener(name, listener);
  }

  public void removeErrorListener(String name) {
    eventLoop.removeErrorListener(name);
  }

  public void removeAllErrorListeners() {
    eventLoop.removeAllErrorListeners();
  }

  public Status status() throws IOException {
    return Status.builder()
      .pid(process.getPid())
      .winSize(process.getWinSize())
      .result(eventLoop.getResult())
      .eventLoopRunning(eventLoop.isRunning())
      .eventLoopException(eventLoop.getThrowable())
      .inputRunning(input.isRunning())
      .inputException(input.getThrowable())
      .outputRunning(output.isRunning())
      .outputException(output.getThrowable())
      .errorRunning(error.isRunning())
      .errorException(error.getThrowable())
      .build();
  }

  public void stop() {
    process.destroy();
  }

  public void input(byte[] bytes) {
    input.getInput().add(bytes);
  }
}
