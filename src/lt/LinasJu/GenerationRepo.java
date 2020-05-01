package lt.LinasJu;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GenerationRepo {

    public static String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    public void generateRandomNetwork(String savingFolder, Date timeOfCreation) {
        CmdRepo cmd = new CmdRepo();
        PrintWriter writer = cmd.startCmd(savingFolder);
        cmd.runCommand(writer, getRandomNetworkCreationCommand(timeOfCreation));
    }

    private String getRandomNetworkCreationCommand(Date timeOfCreation) {

        String outputFileName =
                String.format(
                        SumoCommandsEnum.NETWORK_OUTPUT_FILE_NAME.toString(),
                        new SimpleDateFormat(TIME_FORMAT).format(timeOfCreation));

        Random rand = new Random();
        int iterations = rand.nextInt(1000);

    return SumoCommandsEnum.NETGENERATE.toString()
        + NetworkGenerationCommands.RAND.toString()
        + NetworkGenerationCommands.RAND_ITERATIONS.toString()
        + iterations
        + outputFileName;
  }
}
