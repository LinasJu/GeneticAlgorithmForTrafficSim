package lt.LinasJu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CreationRepo {
  CmdRepo cmd = new CmdRepo();

  public String generateRandomNetwork(String fileName) {
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
   * @param randomTripsLocation location of randomTrips.py program
   * @param networkFileName network file name to make route from
   * @param fringeFactor increases the probability that trips will start/end at the fringe of the
   *     network. If the value 10 is given, edges that have no successor or no predecessor will be
   *     10 times more likely to be chosen as start- or endpoint of a trip. if null - no fringe
   */
  public String generateRandomRoutes(
      String randomTripsLocation, String networkFileName, Float fringeFactor) {
    return generateRandomRoutes(
        randomTripsLocation, networkFileName, networkFileName, fringeFactor);
  }

  /**
   * @param randomTripsLocation location of randomTrips.py program
   * @param networkFileName network file name to make route from
   */
  public String generateRandomRoutes(String randomTripsLocation, String networkFileName) {
    return generateRandomRoutes(randomTripsLocation, networkFileName, networkFileName, null);
  }

  /**
   * @param randomTripsLocation location of randomTrips.py program
   * @param networkFileName network file name to make route from
   * @param outputFileName if null - used same as network file name
   * @param fringeFactor increases the probability that trips will start/end at the fringe of the
   *     network. If the value 10 is given, edges that have no successor or no predecessor will be
   *     10 times more likely to be chosen as start- or endpoint of a trip. if null - no fringe
   *     factor
   */
  public String generateRandomRoutes(
      String randomTripsLocation,
      String networkFileName,
      String outputFileName,
      Float fringeFactor) {

    String inputFile = networkFileName.concat(FilesSuffixesEnum.NETWORK.toString());
    String outputFile =
        (outputFileName != null ? outputFileName : networkFileName)
            .concat(
                FilesSuffixesEnum.ROUTES
                    .toString()); // todo jei output file name yra kitoks nei networkFileName

    return buildRandomRoutesCreationCommand(
        randomTripsLocation, inputFile, outputFile, fringeFactor);
  }

  private String buildRandomRoutesCreationCommand(
      String randomTripsLocation, String inputFile, String outputFile, Float fringeFactor) {
    String command = SumoCommandsEnum.PYTHON.toString();
    command =
        command
            .concat(randomTripsLocation)
            .concat(SumoCommandsEnum.RANDOM_TRIPS_GENERATION_APP.toString());
    command =
        command.concat(String.format(SumoCommandsEnum.ROAD_NETWORK_INPUT.toString(), inputFile));
    if (fringeFactor != null) {
      command = command.concat("--fringe-factor " + fringeFactor.toString());
    }
    command = command.concat("-r " + outputFile + "-e 50 -l ");

    return command;
  }

  /**
   * @param networkFileName network file name
   * @param routeFileName route file name
   * @param beginValue beginning of the simulation, default - 0
   * @param endValue end of the simulation, default - 4000
   */
  public void createSumoConfigFile(
      String workingDir,
      String networkFileName,
      String routeFileName,
      Integer beginValue,
      Integer endValue) {
    Objects.requireNonNull(networkFileName, "networkFileName must not be null");
    List<String> fileText =
        buildSumoConfigFileText(networkFileName, routeFileName, beginValue, endValue);
    Path outputFile =
        Paths.get(workingDir + networkFileName + FilesSuffixesEnum.SUMO_CONFIGURATION.toString());

    try {
      Files.write(outputFile, fileText, StandardCharsets.UTF_8);
    } catch (IOException e) {
      System.out.println("Unable to write file.");
      e.printStackTrace();
    }
  }

  /**
   * this method is used, if network file name and route file names are the same (without suffixes)
   *
   * @param networkFileName
   */
  public void createSumoConfigFile(String workingDir, String networkFileName) {
    createSumoConfigFile(workingDir, networkFileName, networkFileName, null, null);
  }

  private List<String> buildSumoConfigFileText(
      String networkFileName, String routeFileName, Integer beginValue, Integer endValue) {
    Objects.requireNonNull(networkFileName, "networkFileName must not be null");
    String networkName = networkFileName.concat(FilesSuffixesEnum.NETWORK.toString());
    String routeName =
        (routeFileName != null ? routeFileName : networkFileName)
            .concat(
                FilesSuffixesEnum.ROUTES
                    .toString()); // todo jei output file name yra kitoks nei networkFileName
    beginValue = beginValue != null ? beginValue : 0;
    endValue = endValue != null ? endValue : 4000;

    return Arrays.asList(
        "<configuration>",
        "<input>",
        "<net-file value=\"" + networkName + "\"/>",
        "<route-files value=\"" + routeName + "\"/>",
        "</input>",
        "<time>",
        "<begin value =\"" + beginValue + "\"/>",
        "<end value=\"" + endValue + "\"/>",
        "</time>",
        "</configuration>");
  }

  public String generateOutputFile(String fileName, SumoOutputDataFilesEnum outputEnum) {
    String outputfile = fileName.concat(outputEnum.getFileEndWithSuffix());
    fileName = fileName.concat(FilesSuffixesEnum.SUMO_CONFIGURATION.toString());
    return SumoCommandsEnum.SUMO
        .toString()
        .concat(" -c ")
        .concat(fileName)
        .concat(outputEnum.toString())
        .concat(outputfile);
  }

  public List<String> generateOutputFiles(
      String fileName, List<SumoOutputDataFilesEnum> outputEnums) {
    List<String> commands = new ArrayList<>();
    outputEnums.forEach(outputEnum -> commands.add(generateOutputFile(fileName, outputEnum)));
    return commands;
  }
}
