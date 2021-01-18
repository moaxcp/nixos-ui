package process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;

/**
 * Wraps a process in three threads for each stream (in, out, err) and provides a non blocking interface to access
 * these streams.
 */
public class NonBlockingProcess {
  private ProcessBuilder builder;
  private Process process;
  private Input processInput;
  private Output processOutput;
  private Output processError;
  private Result processResult;

  private class Input implements Runnable {

    @Getter
    private ConcurrentLinkedQueue<byte[]> input = new ConcurrentLinkedQueue<>();
    private OutputStream processInput;
    @Getter
    private volatile boolean running = false;

    @Getter
    @Setter
    private volatile boolean stopped = false;
    @Getter
    private volatile Exception exception;

    public Input(@NonNull OutputStream processInput) {
      this.processInput = processInput;
    }

    @Override
    public void run() {
      running = true;
      while(stopped == false) {
        byte[] bytes = input.poll();
        if(bytes == null) {
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            exception = e;
            break;
          }
        }
        try {
          processInput.write(bytes);
        } catch (IOException e) {
          exception = e;
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
    @Setter
    public volatile boolean stopped = false;
    @Getter
    private volatile Exception exception;

    public Output(@NonNull InputStream processOutput) {
      this.processOutput = processOutput;
    }

    @Override
    public void run() {
      running = true;
      while(stopped == false) {
        byte[] bytes = new byte[256];
        int read = 0;
        try {
          read = processOutput.read(bytes);
          if(read == -1) {
            break;
          }
        } catch (IOException e) {
          exception = e;
          break;
        }
        output.add(new ReadBytes(bytes, read));
      }
      running = false;
    }
  }

  private class Result implements Runnable {

    private Process process;
    @Getter
    private volatile boolean running;
    @Getter
    private volatile Integer result;
    @Getter
    private Exception exception;

    public Result(@NonNull Process process) {
      this.process = process;
    }

    @Override
    public void run() {
      running = true;
      try {
        result = process.waitFor();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        exception = e;
      }
      running = false;
    }
  }

  @Value
  private class ReadBytes {
    byte[] bytes;
    int read;
  }

  @Value
  public static class Status {
    Integer result;
    boolean inputRunning;
    boolean outputRunning;
    boolean errorRunning;
  }

  public ConcurrentLinkedQueue<byte[]> getInputQueue() {
    return processInput.getInput();
  }

  public ConcurrentLinkedQueue<ReadBytes> getOutputQueue() {
    return processOutput.getOutput();
  }

  public ConcurrentLinkedQueue<ReadBytes> getErrorQueue() {
    return processError.getOutput();
  }

  public Integer getResult() {
    return processResult.getResult();
  }

  public NonBlockingProcess(String... command) {
    builder = new ProcessBuilder(command);
  }

  public void start() throws IOException {
    process = builder.start();
    processInput = new Input(process.getOutputStream());
    processOutput = new Output(process.getInputStream());
    processError = new Output(process.getErrorStream());
    processResult = new Result(process);

    new Thread(processInput).start();
    new Thread(processOutput).start();
    new Thread(processError).start();
    new Thread(processResult).start();
  }

  public Status status() {
    return new Status(processResult.getResult(), processInput.isRunning(), processOutput.isRunning(), processError.isRunning());
  }

  public void stop() {
    process.destroy();
    //wait until all threads have finished
  }
}
