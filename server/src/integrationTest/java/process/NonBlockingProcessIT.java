package process;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class NonBlockingProcessIT {
  @Test
  void nixosVersion() throws IOException {
    NonBlockingProcess process = new NonBlockingProcess("bash", "-c", "nixos-version");
    process.start();
  }
}
