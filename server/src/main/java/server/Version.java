package server;

import lombok.NonNull;
import lombok.Value;

@Value
public class Version {
  int year;
  int month;
  int commits;
  String shortRevision;
  String codeName;

  public Version(@NonNull String nixosVersion) {
    String[] split = nixosVersion.split("\\.");
    if(split.length != 4) {
      throw new IllegalArgumentException("nixosVersion must have 4 parts split by a '.'");
    }
    year = Integer.valueOf(split[0]);
    month = Integer.valueOf(split[1]);
    commits = Integer.valueOf(split[2]);
    String[] last = split[3].split(" ");
    shortRevision = last[0];
    codeName = last[1].substring(1, last[1].length() - 1);
  }

  public String getRelease() {
    return year + "." + month;
  }

  public String getVersion() {
    return toString();
  }

  public String toString() {
    return getRelease() + "." + commits + "." + shortRevision + " (" + codeName + ")";
  }
}
