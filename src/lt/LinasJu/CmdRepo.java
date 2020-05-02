package lt.LinasJu;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

public class CmdRepo {

  /**
   * @param location run cmd at desired location
   * @return cmd at desired location
   */
  private PrintWriter startCmd(String location) {
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

  /**
   * @param location run cmd at desired location
   * @param command run command at desired location in cmd
   */
  public void runCommand(String location, String command) {
    PrintWriter writer = startCmd(location);
    writer.println(command);
    writer.flush();
  }


  /**
   * @param location run cmd at desired location
   * @param commands run commands at desired location in cmd
   */
  public void runCommands(String location, List<String> commands) {
    PrintWriter writer = startCmd(location);
    commands.forEach(writer::println);
    writer.flush();
  }
}
