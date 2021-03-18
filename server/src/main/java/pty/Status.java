package pty;

import com.pty4j.WinSize;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Status {
  int pid;
  WinSize winSize;
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
