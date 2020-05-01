package lt.LinasJu;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomNetworkGenerationRepo {

    public static String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";


    public void generateRandomNetwork(String savingFolder, Date timeOfCreation) {
        RunCmdCommandsRepo runCmd = new RunCmdCommandsRepo();

        Runtime rt = Runtime.getRuntime();
        PrintWriter writer = runCmd.startCmdOnLocation(rt, savingFolder);

        writer.println(getRandomNetworkCreationCommand(timeOfCreation));
        runCmd.closePrintWriter(writer);
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
}
