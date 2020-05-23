package lt.LinasJu;

import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Entities.Network;
import lt.LinasJu.Entities.SimulationOutputData.Vehicle;
import lt.LinasJu.Entities.TlLogics.TlLogic;
import lt.LinasJu.GeneticAlgorithm.GeneticAlgorithmRepo;
import lt.LinasJu.GeneticAlgorithm.GeneticOperators.SelectionType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    public static String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    public static XmlRepo xmlRepo = new XmlRepo();
    public static SimulationDataRepo simulationDataRepo = new SimulationDataRepo();
    public static GeneticAlgorithmRepo gaRepo = new GeneticAlgorithmRepo();

    public static String baseFileName;
    public static String workingDirectory;
    public static String routeFileName;
    public static boolean isImportedNetwork;
    public static int sizeOfPopulation = 10;
    public static long maxIterations = 20;

    public static List<FilesSuffixesEnum> fileTypesToCreateNetworkFrom = Arrays.asList(FilesSuffixesEnum.NODES,
            FilesSuffixesEnum.EDGES,
            FilesSuffixesEnum.TYPE_OF_EDGES,
            FilesSuffixesEnum.CONNECTIONS,
            FilesSuffixesEnum.TRAFFIC_LIGHT_LOGICS);

    public static void main(String[] args) {

        LocalDateTime startOfProgram = LocalDateTime.now();
        if (args.length == 0) {
            throw new NullPointerException("program arguments must not be null! args[0] - working directory, args [1] - (optional), network file");
        }
        getWorkingDirectoryAndFileName(args);
        routeFileName = baseFileName; // route file will not be changed, to get different results on transport travels.

        CreationRepo creationRepo = new CreationRepo(workingDirectory);

        creationRepo.createBaseInputFiles(baseFileName, isImportedNetwork); // creates routes for network and SUMO config file (if no network is declared - then network too)

        List<SumoOutputDataFilesEnum> simulationOutputFileTypes = Collections.singletonList(SumoOutputDataFilesEnum.EMMISION_DATA);//kol kas pakanka emission informacijos

        //generates nodes, edges, connections, traffic light logic and type of edges files
        creationRepo.createPlainOutputFilesForEditingFromNetworkFile(baseFileName);
        //the main network from xml files that will be modified to get the best solution
        Network theNetwork = xmlRepo.getNetworkFromGeneratedXmlNetworkFiles(workingDirectory, baseFileName);

        //creating random population of genes, where first gene will be from network itself
        //Population must be the same while changing genetic operators to compare them objectively
        List<Gene> basePopulationOfGenes = new ArrayList<>(gaRepo.getRandomPopulationOfGenesByTlLogics(theNetwork.getTrafficLightLogics(), sizeOfPopulation));

        for (SelectionType selectionType : SelectionType.values()) {
            List<Gene> populationOfGenes = new ArrayList<>(basePopulationOfGenes);

            List<Map<Gene, Double>> listOfEveryPopulationGenesWithFitnessScore = new ArrayList<>(); // to compare which Traffic light logic is the best
            for (int iterationNo = 0; iterationNo < maxIterations; iterationNo++) {
                System.out.println("Start of iteration " + iterationNo + " from " + maxIterations);

                Map<Gene, Double> populationGenesWithTheirFitnessScore = new HashMap<>();
                for (int geneIteration = 0; geneIteration < populationOfGenes.size(); geneIteration++) {
                    IterationOfGene(creationRepo,
                            simulationOutputFileTypes,
                            theNetwork,
                            populationOfGenes,
                            iterationNo,
                            populationGenesWithTheirFitnessScore,
                            geneIteration);
                }
                System.out.println(iterationNo + " iteration simulations ran successfully.");

                listOfEveryPopulationGenesWithFitnessScore.add(populationGenesWithTheirFitnessScore);
                creationRepo.exportDataToVisualiseToCsv(populationGenesWithTheirFitnessScore, selectionType.toString() + "_GO iteration_" + iterationNo + "_GenaiSuFitnesais.csv");

                System.out.println("Modifying population No. " + iterationNo + "...");
                populationOfGenes = gaRepo.modifyPopulationOfGenes(populationGenesWithTheirFitnessScore, selectionType); //modifying and getting new population of genes to work with in next generation
                System.out.println("Successfully modified population.");
            }

            creationRepo.exportDataToVisualiseToCsv(listOfEveryPopulationGenesWithFitnessScore, selectionType.toString() + "_GeriausiuGenuFitnesas.csv");
        }

        LocalDateTime endOfProgram = LocalDateTime.now();
        System.out.println("Program started: " + startOfProgram + ", program ended" + endOfProgram);
    }

    private static void IterationOfGene(CreationRepo creationRepo, List<SumoOutputDataFilesEnum> simulationOutputFileTypes, Network theNetwork, List<Gene> populationOfGenes, int iterationNo, Map<Gene, Double> populationGenesWithTheirFitnessScore, int geneIteration) {
        System.out.println("    Running generation no.: " + geneIteration + "...");

        String fileName = geneIteration == 0 ? baseFileName : baseFileName + iterationNo;
        Gene gene = populationOfGenes.get(geneIteration);
        creationRepo.createSumoConfigFile(fileName, routeFileName); // 3. setup SUMO configuration file

        creationRepo.runNetworkSimulationAndGetOutput(fileName, simulationOutputFileTypes); //get simulation output

        List<Vehicle> sortedSimulationVehicles = simulationDataRepo.getVehiclesSimulationOutput(workingDirectory, fileName, simulationOutputFileTypes);
        populationGenesWithTheirFitnessScore.put(gene, gaRepo.calculatefitness(sortedSimulationVehicles));

        List<TlLogic> newTlLogics = gaRepo.setNewTlLogicsPhaseDurationsWithGeneValues(gene, theNetwork.getTrafficLightLogics());
        theNetwork.setTrafficLightLogics(newTlLogics);

        if (geneIteration == 0) {
            xmlRepo.saveWholeNewNetworkToXmlFiles(workingDirectory, baseFileName + iterationNo, theNetwork); //export new network to xml files
        } else {
            xmlRepo.saveNewTrafficLightLogicFileFromNetwork(workingDirectory, baseFileName + iterationNo, theNetwork); //export edited network TlLogic to xml file
        }

        creationRepo.createNetworkFromNetworkFiles(baseFileName + iterationNo, fileTypesToCreateNetworkFrom);
        System.out.println("    Generation ran successfully.");
    }

    private static void getWorkingDirectoryAndFileName(String[] args) {
        workingDirectory = args[0];

        isImportedNetwork = args.length == 2;
        if (!isImportedNetwork) {
            List<String> nameAndDir = newFileName(workingDirectory);
            baseFileName = nameAndDir.get(0);
            workingDirectory = nameAndDir.get(1);
        } else {
            baseFileName = args[1];
        }
    }

    public static List<String> newFileName(String workingDir) {
        Date date = new Date();
        String fileName = new SimpleDateFormat(TIME_FORMAT).format(date); // general file name that will be used for same simulation files
        workingDir = workingDir.concat(fileName);

        boolean success = (new File(workingDir)).mkdirs(); // creates new folder to work in, with new simulation files
        if (!success) {
            System.out.println("Directory creation failed");
        }

        workingDir = workingDir.concat("\\");
        return new ArrayList<>(Arrays.asList(fileName, workingDir));
    }
}
