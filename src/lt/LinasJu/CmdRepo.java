package lt.LinasJu;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

public class CmdRepo {

  public PrintWriter startCmd(String location) {
    Runtime rt = Runtime.getRuntime();
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

  public void runCommand(PrintWriter writer, String command) {
    writer.println(command);
    writer.close();
  }

  public void runCommands(PrintWriter writer, List<String> commands) {
   commands.forEach(writer::println);
   writer.close();
  }
}
