package process;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import process.NonBlockingProcess.Status;

public class NonBlockingProcessIT {
  @Test
  void nixosVersion() throws IOException {
    NonBlockingProcess process = new NonBlockingProcess(true, "bash", "-c", "nixos-version");
    process.addOutputListener("test", b -> System.out.print(new String(b)));
    process.start();
    Status status = process.status();
    while(status.getResult() == null) {
      System.out.println(status);
      status = process.status();
    }
    System.out.println(status);
  }
}
