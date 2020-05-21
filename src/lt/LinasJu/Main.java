package lt.LinasJu;

import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Entities.Network;
import lt.LinasJu.Entities.SimulationOutputData.Vehicle;
import lt.LinasJu.Entities.TlLogics.TlLogic;

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
        List<FilesSuffixesEnum> fileTypesToCreateNetworkFrom = Arrays.asList(FilesSuffixesEnum.NODES,
                FilesSuffixesEnum.EDGES,
                FilesSuffixesEnum.TYPE_OF_EDGES,
                FilesSuffixesEnum.CONNECTIONS,
                FilesSuffixesEnum.TRAFFIC_LIGHT_LOGICS);

        //generates nodes, edges, connections, traffic light logic and type of edges files
        creationRepo.createPlainOutputFilesForEditingFromNetworkFile(baseFileName);

        //the main network from xml files that will be modified to get the best solution
        Network theNetwork = xmlRepo.getNetworkFromGeneratedXmlNetworkFiles(workingDirectory, baseFileName);

        //creating random population of genes, where first gene will be from network itself
        List<Gene> populationOfGenes = gaRepo.getRandomPopulationOfGenesByTlLogics(theNetwork.getTrafficLightLogics(), sizeOfPopulation);

        for (int iterationNo = 0; iterationNo < maxIterations; iterationNo++) {

            Map<Gene, Float> populationGenesWithTheirFitnessScore = new HashMap<>();
            for (int geneIteration = 0; geneIteration < populationOfGenes.size(); geneIteration++) {
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
            }
            listOfEveryPopulationGenesWithFitnessScore.add(populationGenesWithTheirFitnessScore);

            populationOfGenes = gaRepo.modifyPopulationOfGenes(populationGenesWithTheirFitnessScore);

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
