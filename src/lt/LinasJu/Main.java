package lt.LinasJu;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {

  public static String TEMP_WORKING_DIRECTORY = "C:\\SumoFiles\\GeneratedNetworks\\";
  public static String RANDOM_TRIPS_LOCATION = "\"C:\\Program Files (x86)\\Eclipse\\Sumo\\tools\"";
  public static String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";

  public static void main(String[] args) {
    CmdRepo cmdRepo = new CmdRepo();
    PrintWriter cmd = cmdRepo.startCmdAtLocation(TEMP_WORKING_DIRECTORY);

    Date date = new Date();
    String fileName = new SimpleDateFormat(TIME_FORMAT).format(date); // general file name that will be used for same simulation files

//    collectConsoleOutputToFile(fileName);
    createInputOutputFiles(cmd, fileName);

    cmd.close();
  }

  public static void collectConsoleOutputToFile(String fileName) {
         PrintStream printStream = null;
    try {
        printStream = new PrintStream(new FileOutputStream(fileName + "debugging.txt"));
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    System.setOut(printStream);
  }

  public static void createInputOutputFiles(PrintWriter cmd, String fileName) {
    CreationRepo creationRepo = new CreationRepo();

    cmd.println(
        creationRepo.generateRandomNetwork(fileName)); // 1. generate random road network file
    cmd.flush();

    cmd.println(
        creationRepo.generateRandomRoutes(
            RANDOM_TRIPS_LOCATION, fileName)); // 2. create random routes from network file
    cmd.flush();

    creationRepo.createSumoConfigFile(
        TEMP_WORKING_DIRECTORY, fileName); // 3. setup SUMO configuration file

    List<SumoOutputDataFilesEnum> outputDataFilesEnums =
        Arrays.asList(
            SumoOutputDataFilesEnum.FCD_TRACE_DATA,
            SumoOutputDataFilesEnum.RAW_VEHICLE_POSITION_DATA,
            SumoOutputDataFilesEnum.EMMISION_DATA);

    List<String> outputs =
        creationRepo.generateOutputFiles(fileName, outputDataFilesEnums); // 4. generate output
    outputs.forEach(
        output -> {
          cmd.println(output);
          cmd.flush();
        });
  }
}
