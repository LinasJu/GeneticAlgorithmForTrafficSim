package lt.LinasJu;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CreationRepo {

    public static String RANDOM_TRIPS_LOCATION = "\"C:\\Program Files (x86)\\Eclipse\\Sumo\\tools\"";

    public String generateRandomNetwork(String fileName) {
        String outputFileName = String.format(SumoCommandsEnum.NETWORK_OUTPUT_FILE_NAME.toString(), fileName);

        Random rand = new Random();
        int iterations = rand.nextInt(1000);

        return SumoCommandsEnum.NETGENERATE.toString() + NetworkGenerationCommands.RAND.toString() + NetworkGenerationCommands.RAND_ITERATIONS.toString() + iterations + outputFileName;
    }

    /**
     * @param randomTripsLocation location of randomTrips.py program
     * @param networkFileName     network file name to make route from
     * @param fringeFactor        increases the probability that trips will start/end at the fringe of the
     *                            network. If the value 10 is given, edges that have no successor or no predecessor will be
     *                            10 times more likely to be chosen as start- or endpoint of a trip. if null - no fringe
     */
    public String generateRandomRoutes(String randomTripsLocation, String networkFileName, Float fringeFactor) {
        return generateRandomRoutes(randomTripsLocation, networkFileName, networkFileName, fringeFactor);
    }

    /**
     * @param randomTripsLocation location of randomTrips.py program
     * @param networkFileName     network file name to make route from
     */
    public String generateRandomRoutes(String randomTripsLocation, String networkFileName) {
        return generateRandomRoutes(randomTripsLocation, networkFileName, networkFileName, null);
    }

    /**
     * @param randomTripsLocation location of randomTrips.py program
     * @param networkFileName     network file name to make route from
     * @param outputFileName      if null - used same as network file name
     * @param fringeFactor        increases the probability that trips will start/end at the fringe of the
     *                            network. If the value 10 is given, edges that have no successor or no predecessor will be
     *                            10 times more likely to be chosen as start- or endpoint of a trip. if null - no fringe
     *                            factor
     */
    public String generateRandomRoutes(String randomTripsLocation, String networkFileName, String outputFileName, Float fringeFactor) {

        String inputFile = networkFileName.concat(FilesSuffixesEnum.NETWORK.toString());
        String outputFile = (outputFileName != null ? outputFileName : networkFileName).concat(FilesSuffixesEnum.ROUTES.toString()); // todo jei output file name yra kitoks nei networkFileName

        return buildRandomRoutesCreationCommand(randomTripsLocation, inputFile, outputFile, fringeFactor);
    }

    private String buildRandomRoutesCreationCommand(String randomTripsLocation, String inputFile, String outputFile, Float fringeFactor) {
        String command = SumoCommandsEnum.PYTHON.toString();
        command = command.concat(randomTripsLocation).concat(SumoCommandsEnum.RANDOM_TRIPS_GENERATION_APP.toString());
        command = command.concat(String.format(SumoCommandsEnum.ROAD_NETWORK_INPUT.toString(), inputFile));
        if (fringeFactor != null) {
            command = command.concat("--fringe-factor " + fringeFactor.toString());
        }
        command = command.concat("-r " + outputFile + "-e 5000 -l ");

        return command;
    }

    /**
     * @param networkFileName network file name
     * @param routeFileName   route file name
     * @param beginValue      beginning of the simulation, default - 0
     * @param endValue        end of the simulation, default - 4000
     */
    public void createSumoConfigFile(String workingDir, String networkFileName, String routeFileName, Integer beginValue, Integer endValue) {
        Objects.requireNonNull(networkFileName, "networkFileName must not be null");
        List<String> fileText = buildSumoConfigFileText(networkFileName, routeFileName, beginValue, endValue);
        Path outputFile = Paths.get(workingDir + networkFileName + FilesSuffixesEnum.SUMO_CONFIGURATION.toString());

        try {
            Files.write(outputFile, fileText, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Unable to write file.");
            e.printStackTrace();
        }
    }

    /**
     * this method is used, if network file name and route file names are the same (without suffixes)
     */
    public void createSumoConfigFile(String workingDir, String networkFileName) {
        createSumoConfigFile(workingDir, networkFileName, networkFileName, null, null);
    }

    private List<String> buildSumoConfigFileText(String networkFileName, String routeFileName, Integer beginValue, Integer endValue) {
        Objects.requireNonNull(networkFileName, "networkFileName must not be null");
        String networkName = networkFileName.concat(FilesSuffixesEnum.NETWORK.toString());
        String routeName = (routeFileName != null ? routeFileName : networkFileName).concat(FilesSuffixesEnum.ROUTES.toString()); // todo jei output file name yra kitoks nei networkFileName
        beginValue = beginValue != null ? beginValue : 0;
        endValue = endValue != null ? endValue : 2000;

        return Arrays.asList("<configuration>", "<input>", "<net-file value=\"" + networkName + "\"/>", "<route-files value=\"" + routeName + "\"/>", "</input>", "<time>", "<begin value =\"" + beginValue + "\"/>", "<end value=\"" + endValue + "\"/>", "</time>", "</configuration>");
    }

    public String generateOutputCommand(String fileName, List<SumoOutputDataFilesEnum> outputDataFilesEnums) {
        List<String> outputFileList = new ArrayList<>();
        for (SumoOutputDataFilesEnum anEnum : outputDataFilesEnums) {
            outputFileList.add(anEnum.toString() + " " + fileName + anEnum.getFileEndWithSuffixXml());
        }

        fileName = fileName.concat(FilesSuffixesEnum.SUMO_CONFIGURATION.toString());

        String outputFiles = "";
        for (String s : outputFileList) {
            outputFiles = outputFiles.concat(" " + s);
        }

        return SumoCommandsEnum.SUMO.toString().concat(" -c ").concat(fileName).concat(outputFiles);
    }

    /**
     *  Checks if network is imported, if not - creates random network. Then creates random routes for network file and SUMO configuration file to run simulation.
     * @param cmd command windows
     * @param workingDir working directory
     * @param fileName base file name
     * @param isImportedNetwork checking if it is needed to generate random network or it is imported
     */
    public void createInputFiles(PrintWriter cmd, String workingDir, String fileName, boolean isImportedNetwork) {

        if (!isImportedNetwork) {
            cmd.println(generateRandomNetwork(fileName)); // 1. generate random road network file
            cmd.flush();
        }

        cmd.println(generateRandomRoutes(RANDOM_TRIPS_LOCATION, fileName)); // 2. create random routes for network file
        cmd.flush();

        createSumoConfigFile(workingDir, fileName); // 3. setup SUMO configuration file
    }

    public void createSimulationOutputData(PrintWriter cmd, String fileName, List<SumoOutputDataFilesEnum> outputDataFilesEnums) {
        String output = generateOutputCommand(fileName, outputDataFilesEnums); // 4. generate output

        cmd.println(output + " > " + fileName + "simulationOutput.txt 2>&1");
        cmd.flush();
    }

    //used for editing. generates nodes, edges, connections, traffic light logic and type of edges files
    public void createPlainOutputFilesForEditing(PrintWriter cmd, String fileName) {
        cmd.println(SumoCommandsEnum.NETCONVERT.toString() + String.format(SumoCommandsEnum.SUMO_NET_FILE_INPUT.toString(), fileName + FilesSuffixesEnum.NETWORK.toString()) + SumoOutputDataFilesEnum.OUTPUT_FOR_EDITING.toString() + fileName + SumoOutputDataFilesEnum.OUTPUT_FOR_EDITING.getFileEnd());
        cmd.flush();
    }

    public void createFile(String fileName) {
        try {
            File myObj = new File(fileName);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File " + myObj.getName() + " already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
