package process;

import lombok.Value;

@Value
class ReadBytes {
  byte[] bytes;
  int read;
}
