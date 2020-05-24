package lt.LinasJu;

import lombok.SneakyThrows;
import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Entities.Network;
import lt.LinasJu.Entities.SimulationOutputData.Vehicle;
import lt.LinasJu.Entities.TlLogics.TlLogic;
import lt.LinasJu.GeneticAlgorithm.GeneticAlgorithmRepo;
import lt.LinasJu.GeneticAlgorithm.GeneticOperators.SelectionType;
import lt.LinasJu.Utils.MapUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public static int sizeOfPopulation = 30;
    public static long maxIterations = 100;

    public static List<FilesSuffixesEnum> fileTypesToCreateNetworkFrom = Arrays.asList(FilesSuffixesEnum.NODES,
            FilesSuffixesEnum.EDGES,
            FilesSuffixesEnum.TYPE_OF_EDGES,
            FilesSuffixesEnum.CONNECTIONS,
            FilesSuffixesEnum.TRAFFIC_LIGHT_LOGICS);
    public static List<SumoOutputDataFilesEnum> simulationOutputFileTypes = Collections.singletonList(SumoOutputDataFilesEnum.EMMISION_DATA);//kol kas pakanka emission informacijos

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println(">>>>>>>>>>>>>>population size:" + sizeOfPopulation + " iterations:" + maxIterations + "<<<<<<<<<<<<<<<<");
        LocalDateTime startOfProgram = LocalDateTime.now();

        String startOfProgramString = startOfProgram.format(DateTimeFormatter.ofPattern("yyyy_MM_dd.H_m"));
        if (args.length == 0) {
            throw new NullPointerException("program arguments must not be null! args[0] - working directory, args [1] - (optional), network file");
        }
        getWorkingDirectoryAndFileName(args);
        routeFileName = baseFileName; // route file will not be changed, to get different results on transport travels.

        CreationRepo creationRepo = new CreationRepo(workingDirectory);

        creationRepo.createBaseInputFiles(baseFileName, isImportedNetwork); // creates routes for network and SUMO config file (if no network is declared - then network too)

        //generates nodes, edges, connections, traffic light logic and type of edges files
        creationRepo.createPlainOutputFilesForEditingFromNetworkFile(baseFileName);
        //the main network from xml files that will be modified to get the best solution
        Network theNetwork = xmlRepo.getNetworkFromGeneratedXmlNetworkFiles(workingDirectory, baseFileName);

        //creating random population of genes, where first gene will be from network itself
        //Population must be the same while changing genetic operators to compare them objectively
        List<Gene> basePopulationOfGenes = new ArrayList<>(gaRepo.getRandomPopulationOfGenesByTlLogics(theNetwork.getTrafficLightLogics(), sizeOfPopulation));

        Map<Gene, Double> basePopulationGenesWithFitnesses = new HashMap<>();

        basePopulationOfGenes.forEach(gene -> {
            updateTllCreateFilesRunSimulation(creationRepo, theNetwork, baseFileName, gene);
            List<Vehicle> simulationVehicles = simulationDataRepo.getVehiclesSimulationOutput(workingDirectory, baseFileName, simulationOutputFileTypes);

            basePopulationGenesWithFitnesses.put(gene, gaRepo.calculatefitness(simulationVehicles));
        });

        for (SelectionType selectionType : SelectionType.values()) {
            Map<Gene, Double> populationGenesWithFitnesses = new LinkedHashMap<>(basePopulationGenesWithFitnesses); //kad nereiktu perskaiciuoti tos pacios pirmos Sumos

            List<Double> listOfPopulationsIterationsfitnessesSum = new ArrayList<>();
            listOfPopulationsIterationsfitnessesSum.add(basePopulationGenesWithFitnesses.values().stream().mapToDouble(Double::doubleValue).sum());
            List<Double> listOfPopulationsIterationsfitnessesMax = new ArrayList<>();
            listOfPopulationsIterationsfitnessesMax.add(Collections.max(basePopulationGenesWithFitnesses.values()));

            for (int iterationNo = 0; iterationNo < maxIterations; iterationNo++) {
                String fileName = iterationNo == 0 ? baseFileName : baseFileName + startOfProgramString + iterationNo;

                Gene newModifiedGene = gaRepo.getModifiedGeneFromPopulation(populationGenesWithFitnesses, selectionType); //modifying and getting new population of genes to work with in next generation

                updateTllCreateFilesRunSimulation(creationRepo, theNetwork, fileName, newModifiedGene);
                List<Vehicle> sortedSimulationVehicles = simulationDataRepo.getVehiclesSimulationOutput(workingDirectory, fileName, simulationOutputFileTypes);

                populationGenesWithFitnesses = MapUtils.sortByValueDesc(populationGenesWithFitnesses);

                Gene geneToRemove = new Gene();
                //gausime paskutini gena
                for (Map.Entry<Gene, Double> entry : populationGenesWithFitnesses.entrySet()) {
                    geneToRemove = entry.getKey();
                }

                populationGenesWithFitnesses.remove(geneToRemove);
                populationGenesWithFitnesses.put(newModifiedGene, gaRepo.calculatefitness(sortedSimulationVehicles));

                listOfPopulationsIterationsfitnessesSum.add(populationGenesWithFitnesses.values().stream().mapToDouble(Double::doubleValue).sum());
                listOfPopulationsIterationsfitnessesMax.add(Collections.max(populationGenesWithFitnesses.values()));
            }
            creationRepo.writingDataToFile(listOfPopulationsIterationsfitnessesSum, startOfProgramString + " " + selectionType.toString() + "_GO_populations" + sizeOfPopulation + "_iterations" + maxIterations +"_GenuFitnesuSum.csv");
            creationRepo.writingDataToFile(listOfPopulationsIterationsfitnessesMax, startOfProgramString + " " + selectionType.toString() + "_GO_populations" + sizeOfPopulation + "_iterations" + maxIterations +"_GenuFitnesuMax.csv");
        }

        LocalDateTime endOfProgram = LocalDateTime.now();
        System.out.println("Program started: " + startOfProgram + ", program ended" + endOfProgram);
    }

    private static void updateTllCreateFilesRunSimulation(CreationRepo creationRepo, Network theNetwork, String fileName, Gene newModifiedGene) {
        List<TlLogic> newTlLogics = gaRepo.setNewTlLogicsPhaseDurationsWithGeneValues(newModifiedGene, theNetwork.getTrafficLightLogics());
        theNetwork.setTrafficLightLogics(newTlLogics);
        xmlRepo.saveWholeNewNetworkToXmlFiles(workingDirectory, fileName, theNetwork); //export new network to xml files

        creationRepo.createNetworkFromNetworkFiles(fileName, fileTypesToCreateNetworkFrom);
        creationRepo.createSumoConfigFile(fileName, routeFileName); // 3. setup SUMO configuration file
        creationRepo.runNetworkSimulationAndGetOutput(fileName, simulationOutputFileTypes); //get simulation output
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
