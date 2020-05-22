package lt.LinasJu;

import lombok.SneakyThrows;
import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Utils.ShellExec;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CreationRepo {
    public static String RANDOM_TRIPS_LOCATION = "C:\\Program Files (x86)\\Eclipse\\Sumo\\tools";

    private String workingDir;

    ShellExec shellExec = new ShellExec(true, true);

    public CreationRepo() {
        this("C:\\");
    }

    public CreationRepo(String workingDir) {
        this.workingDir = workingDir;
    }

    public String[] getCommandNetgenerateRandomNetworkArgs(String fileName) {
        String outputFileNameArg = String.format(SumoCommandsEnum.NETWORK_OUTPUT_FILE_NAME.toString(), fileName);

        Random rand = new Random();
        int iterations = rand.nextInt(1000);

        return new String[]{NetworkGenerationCommands.RAND.toString() + NetworkGenerationCommands.RAND_ITERATIONS.toString() + iterations,
                outputFileNameArg};
    }

    /**
     * @param networkFileName     network file name to make route from
     * @param fringeFactor        increases the probability that trips will start/end at the fringe of the
     *                            network. If the value 10 is given, edges that have no successor or no predecessor will be
     *                            10 times more likely to be chosen as start- or endpoint of a trip. if null - no fringe
     */
    public String[] generateRandomRoutesCommandArgs(String networkFileName, Float fringeFactor) {
        return generateRandomRoutesCommandArgs(networkFileName, networkFileName, fringeFactor);
    }

    /**
     * @param networkFileName     network file name to make route from
     */
    public String[] getRandomRoutesCommandArgs(String networkFileName) {
        return generateRandomRoutesCommandArgs(networkFileName, networkFileName, null);
    }

    /**
     * @param networkFileName     network file name to make route from
     * @param outputFileName      if null - used same as network file name
     * @param fringeFactor        increases the probability that trips will start/end at the fringe of the
     *                            network. If the value 10 is given, edges that have no successor or no predecessor will be
     *                            10 times more likely to be chosen as start- or endpoint of a trip. if null - no fringe
     *                            factor
     */
    public String[] generateRandomRoutesCommandArgs(String networkFileName, String outputFileName, Float fringeFactor) {
        List<String> args = new ArrayList<>();

        args.add(RANDOM_TRIPS_LOCATION.concat(SumoCommandsEnum.RANDOM_TRIPS_GENERATION_APP.toString())); //Random trip generator location

        args.add(SumoCommandsEnum.ROAD_NETWORK_INPUT.toString()); // network input argument
        args.add(networkFileName.concat(FilesSuffixesEnum.NETWORK.toString())); //Network input file

        if (fringeFactor != null) {
            args.add("--fringe-factor=" + fringeFactor.toString()); // setting a high value will generated lots of through-traffic which is plausible for small networks
        }

        args.add("-r"); //output argument
        args.add((outputFileName != null ? outputFileName : networkFileName).concat(FilesSuffixesEnum.ROUTES.toString()));//Route output file todo jei output file name yra kitoks nei networkFileName
        args.add("-e");
        args.add("5000");
        args.add("-l");
        return args.toArray(new String[0]);
    }


    /**
 * @param networkFileName network file name
     * @param routeFileName   route file name
     */
    public void createSumoConfigFile(String networkFileName, String routeFileName) {
        createSumoConfigFile(networkFileName, routeFileName, null, null);
    }

    /**
     * @param networkFileName network file name
     * @param routeFileName   route file name
     * @param beginValue      beginning of the simulation, default - 0
     * @param endValue        end of the simulation, default - 4000
     */
    public void createSumoConfigFile(String networkFileName, String routeFileName, Integer beginValue, Integer endValue) {
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
    public void createSumoConfigFile(String networkFileName) {
        createSumoConfigFile(networkFileName, networkFileName, null, null);
    }

    private List<String> buildSumoConfigFileText(String networkFileName, String routeFileName, Integer beginValue, Integer endValue) {
        Objects.requireNonNull(networkFileName, "networkFileName must not be null");
        String networkName = networkFileName.concat(FilesSuffixesEnum.NETWORK.toString());
        String routeName = (routeFileName != null ? routeFileName : networkFileName).concat(FilesSuffixesEnum.ROUTES.toString()); // todo jei output file name yra kitoks nei networkFileName
        beginValue = beginValue != null ? beginValue : 0;
        endValue = endValue != null ? endValue : 1000;

        return Arrays.asList("<configuration>", "<input>", "<net-file value=\"" + networkName + "\"/>", "<route-files value=\"" + routeName + "\"/>", "</input>", "<time>", "<begin value =\"" + beginValue + "\"/>", "<end value=\"" + endValue + "\"/>", "</time>", "</configuration>");
    }

    public List<String> getSimulationOutputCommandArgs(String fileName, List<SumoOutputDataFilesEnum> outputDataFilesEnums) {
        List<String> args = new ArrayList<>();
        args.add("-c"); //Sumo configuration argument
        args.add(fileName.concat(FilesSuffixesEnum.SUMO_CONFIGURATION.toString() + " "));

        for (SumoOutputDataFilesEnum anEnum : outputDataFilesEnums) {
            args.add(anEnum.toString());
            args.add(fileName + anEnum.getFileEndWithSuffixXml());
        }
        return args;
    }

    /**
     *  Checks if network is imported, if not - creates random network. Then creates random routes for network file and SUMO configuration file to run simulation.
     * @param fileName base file name
     * @param isImportedNetwork checking if it is needed to generate random network or it is imported
     */
    @SneakyThrows
    public void createBaseInputFiles(String fileName, boolean isImportedNetwork) {

        if (!isImportedNetwork) {
            shellExec.execute(SumoCommandsEnum.NETGENERATE.toString(), workingDir, true, getCommandNetgenerateRandomNetworkArgs(fileName)); // 1. generate random road network file
        }

        shellExec.execute(SumoCommandsEnum.PYTHON.toString(), workingDir, true, getRandomRoutesCommandArgs(fileName)); // 2. create random routes for network file
        System.out.println(shellExec.getOutput());
        System.out.println(shellExec.getError());
    }

    @SneakyThrows
    public void runNetworkSimulationAndGetOutput(String fileName, List<SumoOutputDataFilesEnum> outputDataFilesEnums) {
//        String simulationOutputFileName = fileName + "SimulationOutput.txt"; for debugging
//        createEmptyFile(workingDir + simulationOutputFileName);
        List<String> simulationArgs = getSimulationOutputCommandArgs(fileName, outputDataFilesEnums);
        shellExec.execute(SumoCommandsEnum.SUMO.toString(), workingDir, true, simulationArgs.toArray(String[]::new)); // 4. run simulation and generate output
//        System.out.println(shellExec.getOutput()); used for debugging
//        System.out.println(shellExec.getError());
    }

    //used for editing. generates nodes, edges, connections, traffic light logic and type of edges files
    @SneakyThrows
    public void createPlainOutputFilesForEditingFromNetworkFile(String fileName) {
        List<String> args = new ArrayList<>();
        args.add(SumoCommandsEnum.SUMO_NET_FILE_INPUT_FULL.toString());
        args.add(fileName + FilesSuffixesEnum.NETWORK.toString());
        args.add(SumoOutputDataFilesEnum.OUTPUT_FOR_EDITING.toString() + fileName + SumoOutputDataFilesEnum.OUTPUT_FOR_EDITING.getFileEnd());

        shellExec.execute(SumoCommandsEnum.NETCONVERT.toString(), workingDir, true, args.toArray(String[]::new));
//        System.out.println(shellExec.getOutput()); used for debugging
//        System.out.println(shellExec.getError());
    }

    public void createEmptyFile(String fileNameWithPath) {
        try {
            File myObj = new File(fileNameWithPath);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
//                System.out.println("File " + myObj.getName() + " already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public void createNetworkFromNetworkFiles(String fileName, List<FilesSuffixesEnum> usedXmlFiles) {
        List<String> args = new ArrayList<>();
        for (FilesSuffixesEnum suffix : usedXmlFiles) {
            args.add(suffix.getDeclarationCommand() + fileName + suffix.toString());
        }

        args.add(SumoCommandsEnum.NETWORK_OUTPUT_FILE_NAME.toString());
        args.add(fileName + FilesSuffixesEnum.NETWORK.toString());

        shellExec.execute(SumoCommandsEnum.NETCONVERT.toString(), workingDir, true, args.toArray(String[]::new));
//        System.out.println(shellExec.getOutput()); used for debugging
//        System.out.println(shellExec.getError());
    }

    @SneakyThrows
    public void exportDataToVisualiseToCsv(List<Map<Gene, Double>> listOfEveryPopulationGenesWithFitnessScore, String fileName) {
        List<Double> resultList = getBestIterationsPopulationsFitnesses(listOfEveryPopulationGenesWithFitnessScore);

        FileWriter fileWriter = new FileWriter(fileName);

        try {
            for (int iteration = 0; iteration < resultList.size(); iteration++) {
                Double result = resultList.get(iteration);
                fileWriter.append(String.valueOf(iteration));
                fileWriter.append(',');
                fileWriter.append(result.toString());
                fileWriter.append('\n');
            }

            System.out.println("Write CSV successfully!");
        } catch (Exception e) {
            System.out.println("Writing CSV error!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Flushing/closing error!");
                e.printStackTrace();
            }
        }
    }

    private List<Double> getBestIterationsPopulationsFitnesses(List<Map<Gene, Double>> listOfEveryPopulationGenesWithFitnessScore) {
        List<Double> resultList = new ArrayList<>();
        listOfEveryPopulationGenesWithFitnessScore.forEach(geneDoubleMap -> {
            Double biggestFitnessScoreOfPopulation = 0d;

            for (Map.Entry<Gene, Double> entry : geneDoubleMap.entrySet()) {
                Double aDouble = entry.getValue();
                if (aDouble > biggestFitnessScoreOfPopulation) {
                    biggestFitnessScoreOfPopulation = aDouble;
                }
            }
            resultList.add(biggestFitnessScoreOfPopulation);
        });
        return resultList;
    }

}
