package lt.LinasJu;

import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Entities.Network;
import lt.LinasJu.Entities.SimulationOutputData.Vehicle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    public static String baseFileName;
    public static String workingDirectory;
    public static String routeFileName;
    public static boolean isImportedNetwork;

    private static List<Map<Gene, Float>> listOfEveryPopulationGenesWithFitnessScore = new ArrayList<>();// to compare which Traffic light logic is the best

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new NullPointerException("program arguments must not be null! args[0] - working directory, args [1] - (optional), network file");
        }
        getWorkingDirectoryAndFileName(args);
        routeFileName = baseFileName; // route file will not be changed, to get different results on transport travels.

        int sizeOfPopulation = 20;
        long maxIterations = 100;

        XmlRepo xmlRepo = new XmlRepo();
        SimulationDataRepo simulationDataRepo = new SimulationDataRepo();
        GeneticAlgorithmRepo gaRepo = new GeneticAlgorithmRepo();
        CreationRepo creationRepo = new CreationRepo(workingDirectory);

        creationRepo.createBaseInputFiles(baseFileName, isImportedNetwork); // creates routes for network and SUMO config file (if no network is declared - then network too)

        List<SumoOutputDataFilesEnum> simulationOutputFileTypes = Collections.singletonList(SumoOutputDataFilesEnum.EMMISION_DATA);//kol kas pakanka emission informacijos
        List<FilesSuffixesEnum> fileTypesToCreateNetworkFrom = Arrays.asList(FilesSuffixesEnum.NODES, FilesSuffixesEnum.EDGES, FilesSuffixesEnum.TYPE_OF_EDGES, FilesSuffixesEnum.CONNECTIONS, FilesSuffixesEnum.TRAFFIC_LIGHT_LOGICS);

        creationRepo.createPlainOutputFilesForEditingFromNetworkFile(baseFileName); //generates nodes, edges, connections, traffic light logic and type of edges files
        Network theNetwork = xmlRepo.getNetworkFromGeneratedXmlNetworkFiles(workingDirectory, baseFileName);

        List<Gene> populationOfGenes = gaRepo.getRandomPopulationOfGenesByTlLogics(theNetwork.getTrafficLightLogics(), sizeOfPopulation);

        for (int iterationNo = 0; iterationNo < maxIterations; iterationNo++) { //arba perdaryti kad kai atsakymų skirtumai yra mažesni nei epsilon
            String fileName = iterationNo == 0 ? baseFileName : baseFileName + iterationNo;

            Map<Gene, Float> populationGenesWithTheirFitnessScore = new HashMap<>();
            for (Gene gene : populationOfGenes) {
                creationRepo.createSumoConfigFile(fileName, routeFileName); // 3. setup SUMO configuration file

                creationRepo.runNetworkSimulationAndGetOutput(fileName, simulationOutputFileTypes); //get simulation output
                //creationRepo.createPlainOutputFilesForEditingFromNetworkFile(fileName); //generates nodes, edges, connections, traffic light logic and type of edges files

                List<Vehicle> sortedSimulationVehicles = simulationDataRepo.getVehiclesSimulationOutput(workingDirectory, fileName, simulationOutputFileTypes);
                populationGenesWithTheirFitnessScore.put(gene, gaRepo.calculatefitness(sortedSimulationVehicles));


//todo
                xmlRepo.saveNetworkToXmlFiles(workingDirectory, fileName, theNetwork); //export edited network to xml files
                creationRepo.createNetworkFromNetworkFiles(fileName, fileTypesToCreateNetworkFrom);
            }
            listOfEveryPopulationGenesWithFitnessScore.add(populationGenesWithTheirFitnessScore);

            //todo edit network with genetic algorithm (fitness function, atrinkimas, kryzminimas, mutacija)

        }
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
