package lt.LinasJu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CreationRepo {
  CmdRepo cmd = new CmdRepo();


  public void generateRandomNetwork(String workingDir, String fileName) {
    CmdRepo cmd = new CmdRepo();
    cmd.runCommand(workingDir, buildRandomNetworkCreationCommand(fileName));
  }

  private String buildRandomNetworkCreationCommand(String fileName) {
    String outputFileName =
        String.format(SumoCommandsEnum.NETWORK_OUTPUT_FILE_NAME.toString(), fileName);

    Random rand = new Random();
    int iterations = rand.nextInt(1000);

    return SumoCommandsEnum.NETGENERATE.toString()
        + NetworkGenerationCommands.RAND.toString()
        + NetworkGenerationCommands.RAND_ITERATIONS.toString()
        + iterations
        + outputFileName;
  }

  /**
 * @param workingDir working directory where to run cmd
   * @param randomTripsLocation location of randomTrips.py program
 * @param networkFileName network file name to make route from
 * @param fringeFactor increases the probability that trips will start/end at the fringe of the
   *     network. If the value 10 is given, edges that have no successor or no predecessor will be
   *     10 times more likely to be chosen as start- or endpoint of a trip. if null - no fringe
   */
  public void generateRandomRoutes(String workingDir, String randomTripsLocation, String networkFileName, Float fringeFactor) {
    generateRandomRoutes(workingDir, randomTripsLocation, networkFileName, networkFileName, fringeFactor);
  }

  /**
   * @param workingDir working directory where to run cmd
   * @param randomTripsLocation location of randomTrips.py program
   * @param networkFileName network file name to make route from
   * @param outputFileName if null - used same as network file name
   * @param fringeFactor increases the probability that trips will start/end at the fringe of the
   *     network. If the value 10 is given, edges that have no successor or no predecessor will be
   *     10 times more likely to be chosen as start- or endpoint of a trip. if null - no fringe
   *     factor
   */
  public void generateRandomRoutes(
      String workingDir,
      String randomTripsLocation,
      String networkFileName,
      String outputFileName,
      Float fringeFactor) {

    String inputFile = networkFileName.concat(FilesSuffixesEnum.NETWORK.toString());
    String outputFile = (outputFileName != null ? outputFileName : networkFileName).concat(FilesSuffixesEnum.ROUTES.toString()); //todo jei output file name yra kitoks nei networkFileName

    String command = buildRandomRoutesCreationCommand(randomTripsLocation, inputFile, outputFile, fringeFactor);

    cmd.runCommand(workingDir, command);
  }

  private String buildRandomRoutesCreationCommand(String randomTripsLocation, String inputFile, String outputFile, Float fringeFactor) {
    String command = SumoCommandsEnum.PYTHON.toString();
    command = command.concat(randomTripsLocation).concat(SumoCommandsEnum.RANDOM_TRIPS_GENERATION_APP.toString());
    command = command.concat(String.format(SumoCommandsEnum.ROAD_NETWORK_INPUT.toString(), inputFile));
    command = command.concat("-r " + outputFile + "-e 50 -l");
    if (fringeFactor != null) {
      command = command.concat("--fringe-factor " + fringeFactor.toString());
    }

    return command;
  }

  /**
   *
   * @param networkFileName network file name
   * @param routeFileName route file name
   * @param beginValue beginning of the simulation, default - 0
   * @param endValue end of the simulation, default - 4000
   */
  public void generateSumoConfigFile(String networkFileName, String routeFileName, Integer beginValue, Integer endValue) {
    Objects.requireNonNull(networkFileName, "networkFileName must not be null");
    List<String> fileText = buildSumoConfigFileText(networkFileName, routeFileName, beginValue, endValue);
    Path outputFile = Paths.get(networkFileName + FilesSuffixesEnum.SUMO_CONFIGURATION.toString());

    try {
      Files.write(outputFile, fileText, StandardCharsets.UTF_8);
    } catch (IOException e) {
      System.out.println("Unable to write file.");
      e.printStackTrace();
    }
  }

  /**
   * this method is used, if network file name and route file names are the same (without suffixes)
   * @param networkFileName
   */
  public void generateSumoConfigFile(String networkFileName) {
    generateSumoConfigFile(networkFileName, networkFileName, null, null);
  }

  private List<String> buildSumoConfigFileText(String networkFileName, String routeFileName, Integer beginValue, Integer endValue) {
    Objects.requireNonNull(networkFileName, "networkFileName must not be null");
    String networkName = networkFileName.concat(FilesSuffixesEnum.NETWORK.toString());
    String routeName = (routeFileName != null ? routeFileName : networkFileName).concat(FilesSuffixesEnum.ROUTES.toString()); //todo jei output file name yra kitoks nei networkFileName
    beginValue = beginValue != null ? beginValue : 0;
    endValue = endValue != null ? endValue : 4000;

    List<String> fileText =
            Arrays.asList(
                    "<configuration>\n",
                    "<input>\n",
                    "<net-file value=\"" + networkName +"\"/>\n",
                    "<route-files value=\"" + routeName +"\"/>\n",
                    "</input>\n",
                    "<time>\n",
                    "<begin value =\"" + beginValue + "\"/>\n",
                    "<end value=\"" + endValue + "\"/>\n",
                    "</time>\n",
                    "</configuration>");
    return fileText;
  }

  public void generateOutputFile(String workingDir, String fileName, SumoOutputDataFilesEnum outputEnum) {
    fileName = fileName.concat(FilesSuffixesEnum.SUMO_CONFIGURATION.toString());
    String outputfile = fileName.concat(outputEnum.getFileEndWithSuffix());
    String generationCommand = SumoCommandsEnum.SUMO.toString().concat("-c ").concat(fileName).concat(outputEnum.toString()).concat(outputfile);
    cmd.runCommand(workingDir, generationCommand);
  }

  public void generateOutputFiles(String workingDir, String fileName, List<SumoOutputDataFilesEnum> outputEnums) {
    outputEnums.forEach(outputEnum -> generateOutputFile(workingDir, fileName, outputEnum));
  }
}
