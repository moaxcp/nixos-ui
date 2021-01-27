package process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

/**
 * Wraps a process in three threads for each stream (in, out, err) and provides a non blocking interface to access
 * these streams.
 */
public class NonBlockingProcess {
  private final ProcessBuilder builder;
  private final boolean redirectError;
  private Process process;
  private Input processInput;
  private Output processOutput;
  private Output processError;
  private EventLoop processEventLoop;
  private ConcurrentHashMap<String, Consumer<byte[]>> outputListeners = new ConcurrentHashMap<>();
  private ConcurrentHashMap<String, Consumer<byte[]>> errorListeners = new ConcurrentHashMap<>();

  private class Input implements Runnable {

    @Getter
    private ConcurrentLinkedQueue<byte[]> input = new ConcurrentLinkedQueue<>();
    private OutputStream processInput;
    @Getter
    private volatile boolean running = false;
    @Getter
    private volatile Throwable throwable;

    public Input(@NonNull OutputStream processInput) {
      this.processInput = processInput;
    }

    @Override
    public void run() {
      running = true;
      while(process.isAlive()) {
        byte[] bytes = input.poll();
        if(bytes == null) {
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

  private class Output implements Runnable {

    @Getter
    private ConcurrentLinkedQueue<ReadBytes> output = new ConcurrentLinkedQueue<>();
    private InputStream processOutput;
    @Getter
    private volatile boolean running = false;
    @Getter
    private volatile Throwable throwable;

    public Output(@NonNull InputStream processOutput) {
      this.processOutput = processOutput;
    }

    @Override
    public void run() {
      running = true;
      while(true) {
        byte[] bytes = new byte[256];
        int read = 0;
        try {
          read = processOutput.read(bytes);
          if(read == -1) {
            break;
          }
        } catch (Throwable e) {
          throwable = e;
          break;
        }
        output.add(new ReadBytes(bytes, read));
      }
      running = false;
    }
  }

  private class EventLoop implements Runnable {

    private Process process;
    @Getter
    private volatile boolean running;
    @Getter
    private volatile Integer result;
    @Getter
    private Throwable throwable;

    public EventLoop(@NonNull Process process) {
      this.process = process;
    }

    @Override
    public void run() {
      running = true;
      try {
        while(processInput.isRunning() || processOutput.isRunning() || processError.isRunning()) {
          boolean hadOutput = sendOutput();
          boolean hadError = sendError();
          if(!(hadOutput || hadError)) {
            Thread.sleep(10);
          }
        }
        sendOutput();
        sendError();
        result = process.waitFor();
      } catch (Throwable e) {
        Thread.currentThread().interrupt();
        throwable = e;
      }
      running = false;
    }

    private boolean sendOutput() {
      boolean hadOutput = false;
      while(!processOutput.getOutput().isEmpty()) {
        ReadBytes bytes = processOutput.getOutput().poll();
        if (bytes != null) {
          hadOutput = true;
        }
        for (Consumer<byte[]> consumer : outputListeners.values()) {
          consumer.accept(Arrays.copyOfRange(bytes.getBytes(), 0, bytes.getRead()));
        }
      }
      return hadOutput;
    }

    private boolean sendError() {
      boolean hadOutput = false;
      while(!processError.getOutput().isEmpty()) {
        ReadBytes bytes = processError.getOutput().poll();
        if (bytes != null) {
          hadOutput = true;
        }
        for (Consumer<byte[]> consumer : errorListeners.values()) {
          consumer.accept(Arrays.copyOfRange(bytes.getBytes(), 0, bytes.getRead()));
        }
      }
      return hadOutput;
    }
  }

  @Value
  @Builder
  public static class Status {
    List<String> command;
    Integer result;
    boolean eventLoopRunning;
    Throwable eventLoopException;
    boolean inputRunning;
    Throwable inputException;
    boolean outputRunning;
    Throwable outputException;
    boolean errorRunning;
    Throwable errorException;
  }

  public NonBlockingProcess(boolean redirectError, String... command) {
    this.redirectError = redirectError;
    builder = new ProcessBuilder(command);
  }

  public void addOutputListener(String name, Consumer<byte[]> consumer) {
    outputListeners.put(name, consumer);
  }

  public void removeOutputListener(String name) {
    outputListeners.remove(name);
  }

  public void addErrorListener(String name, Consumer<byte[]> consumer) {
    errorListeners.put(name, consumer);
  }

  public void removeErrorListener(String name) {
    errorListeners.remove(name);
  }

  public void input(byte[] bytes) {
    processInput.getInput().add(bytes);
  }

  public void start() throws IOException {
    builder.redirectErrorStream(redirectError);
    process = builder.start();
    processInput = new Input(process.getOutputStream());
    processOutput = new Output(process.getInputStream());
    processError = new Output(process.getErrorStream());
    processEventLoop = new EventLoop(process);

    new Thread(processInput).start();
    new Thread(processOutput).start();
    if(!redirectError) {
      new Thread(processError).start();
    }
    new Thread(processEventLoop).start();
  }

  public Status status() {
    return Status.builder().command(builder.command())
    .result(processEventLoop.getResult())
    .eventLoopRunning(processEventLoop.isRunning())
    .eventLoopException(processEventLoop.getThrowable())
    .inputRunning(processInput.isRunning())
    .inputException(processInput.getThrowable())
    .outputRunning(processOutput.isRunning())
    .outputException(processOutput.getThrowable())
    .errorRunning(processError.isRunning())
    .errorException(processError.getThrowable())
    .build();
  }

  public void stop() {
    process.destroy();
    //wait until all threads have finished
  }
}
