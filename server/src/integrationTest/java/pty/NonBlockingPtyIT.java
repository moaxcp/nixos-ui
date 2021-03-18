package pty;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NonBlockingPtyIT {
  @Test
  void nixosVersion() throws IOException {
    NonBlockingPty process = new NonBlockingPty("bash", "-c", "nixos-version");
    AtomicReference<String> output = new AtomicReference<>();
    process.addOutputListener("test", b -> output.set(new String(b)));
    process.start();
    Status status = process.status();
    while(status.getResult() == null) {
      System.out.println(status);
      status = process.status();
    }
    System.out.println(status);
    assertThat(output.get()).startsWith("20.09.3454.60b18a066e8 (Nightingale)");
  }
}
