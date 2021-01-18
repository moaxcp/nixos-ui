package server;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import java.io.IOException;
import java.util.List;
import process.Util;

@Controller
public class OptionsController {
  @Get("/nixos-option/")
  HttpResponse<List<String>> getOptions() throws IOException, InterruptedException {
    String output = Util.execute("bash", "-c", "nixos-option");

    List<String> options = List.of(output.split("\n"));
    return HttpResponse.ok(options);
  }

  @Get("/nixos-option/{option}")
  HttpResponse<List<String>> getOptions(String option) throws IOException, InterruptedException {
    String output = Util.execute("bash", "-c", "nixos-option", option);
    List<String> options = List.of(output.split("\n"));
    return HttpResponse.ok(options);
  }
}
