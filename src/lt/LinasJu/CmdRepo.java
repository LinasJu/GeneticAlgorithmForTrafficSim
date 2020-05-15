package lt.LinasJu;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CmdRepo {

    /**
     * @param location run cmd at desired location
     * @return cmd at desired location
     */
    private List<Object> startCmd(String location) {
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
        stdin.flush();
        return Arrays.asList(stdin, process);
    }

    /**
     * function used if cmd is required to launch separately
     *
     * @param location run cmd at desired location
     * @param command  run command at desired location in cmd
     */
    public void runCommand(String location, String command) {
        List<Object> objects = startCmd(location);
        PrintWriter writer = (PrintWriter) objects.get(0);
        writer.println(command);
        writer.close();
        Process process = (Process) objects.get(1);
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param commands run commands at desired location in cmd
     */
    public void runCommands(PrintWriter cmd, List<String> commands) {
        commands.forEach(command -> {
            cmd.println(command);
            cmd.flush();
        });
    }

    public List<Object> startCmdAtLocation(String location) {
        return startCmd(location);
    }
}
