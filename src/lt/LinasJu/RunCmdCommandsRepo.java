package lt.LinasJu;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class RunCmdCommandsRepo {


  public PrintWriter startCmdOnLocation(Runtime rt, String location) {
    Process process = null;
    try {
      process = rt.exec("cmd");
    } catch (IOException e) {
      e.printStackTrace();
    }

    new Thread(new SyncPipe(Objects.requireNonNull(process).getErrorStream(), System.err)).start();
    new Thread(new SyncPipe(process.getInputStream(), System.out)).start();
    PrintWriter stdin = new PrintWriter(process.getOutputStream());
    stdin.println("cd " + location);
    return stdin;
  }

  public void closePrintWriter(PrintWriter printWriter) {
    printWriter.close();
  }
}
