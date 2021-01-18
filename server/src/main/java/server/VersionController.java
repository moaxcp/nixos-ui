package server;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import java.io.IOException;
import process.Util;

@Controller
public class VersionController {
  @Get("/nixos-version")
  HttpResponse<Version> getVersion() throws IOException, InterruptedException {
    String output = Util.execute("bash", "-c", "nixos-version");
    return HttpResponse.ok(new Version(output));
  }
}
