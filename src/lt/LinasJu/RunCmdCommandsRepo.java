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

  public static String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";

  public void generateRandomNetwork(String savingFolder) {

    Date timeOfCreation = new Date();

    Runtime rt = Runtime.getRuntime();
    PrintWriter writer = startCmdOnLocation(rt, savingFolder);

    writer.println(getRandomNetworkCreationCommand(timeOfCreation));

    closePrintWriter(writer);
  }

  private String getRandomNetworkCreationCommand(Date timeOfCreation) {

    String outputFileName =
        String.format(
            SumoCommands.NETWORK_OUTPUT_FILE_NAME.toString(),
            new SimpleDateFormat(TIME_FORMAT).format(timeOfCreation));

    Random rand = new Random();
    int iterations = rand.nextInt(1000);

    return SumoCommands.NETGENERATE.toString()
        + NetworkGenerationCommands.RAND.toString()
        + NetworkGenerationCommands.RAND_ITERATIONS.toString()
        + iterations
        + outputFileName;
  }

  private PrintWriter startCmdOnLocation(Runtime rt, String location) {
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

  private void closePrintWriter(PrintWriter printWriter) {
    printWriter.close();
  }
}
