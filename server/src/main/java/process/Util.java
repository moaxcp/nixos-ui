package process;

import java.io.IOException;
import java.io.InputStream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {
  public String execute(String... command) throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    Process process = processBuilder.start();
    InputStream in = process.getInputStream();
    InputStream err = process.getErrorStream();
    String error = new String(err.readAllBytes());
    String output = new String(in.readAllBytes()).trim();
    int result = process.waitFor();
    if(result != 0) {
      StringBuilder message = new StringBuilder()
        .append("Wrong result for nixos-version. Expected \"0\" but was \"")
        .append(result)
        .append("\".");
      if(!error.isBlank()) {
        message.append(" Error output:\n")
          .append(error);
      }
      throw new IllegalStateException(message.toString());
    }
    return output;
  }
}
