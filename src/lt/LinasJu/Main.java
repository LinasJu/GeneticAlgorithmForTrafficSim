package lt.LinasJu;

import lt.LinasJu.Entities.Connections.Connection;
import lt.LinasJu.Entities.Edges.Edge;
import lt.LinasJu.Entities.Edges.Roundabout;
import lt.LinasJu.Entities.Network;
import lt.LinasJu.Entities.Nodes.Node;
import lt.LinasJu.Entities.SimulationOutputData.SimulationOutputDataToCompare;
import lt.LinasJu.Entities.TlLogics.TlLogic;
import lt.LinasJu.Entities.TypeOfEdge.Type;
import org.w3c.dom.Document;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    public static String baseFileName;
    public static String workingDirectory;
    public static String routeFileName;
    public static boolean isImportedNetwork;

    private static int populationSize = 5; //generation size
    private static int solutionLength = 10;
    private static double mutationRate = 0.1;
    private static double crossoverRate = 0.5; //optional. usually crossover is always applied
    private static long maxIterations = 1;

    private static Map<Network, SimulationOutputDataToCompare> networksWithComparableSimulationData = new HashMap<>();// to compare which network is the best comparing



    public static void main(String[] args) {
        if (args.length == 0) {
            throw new NullPointerException("program arguments must not be null! args[0] - working directory, args [1] - (optional), network file");
        }
        getWorkingDirectoryAndFileName(args);
        routeFileName = baseFileName; // route file will not be changed, to get different results on transport travels.

        CreationRepo creationRepo = new CreationRepo(workingDirectory);
        XmlRepo xmlRepo = new XmlRepo();
        int bestNetworkId = 0;

        creationRepo.createBaseInputFiles(baseFileName, isImportedNetwork); // creates routes for network and SUMO config file (if no network is declared - then network too)

        List<SumoOutputDataFilesEnum> simulationOutputFileTypes = Arrays.asList(SumoOutputDataFilesEnum.FCD_TRACE_DATA, SumoOutputDataFilesEnum.RAW_VEHICLE_POSITION_DATA, SumoOutputDataFilesEnum.EMMISION_DATA);
        List<FilesSuffixesEnum> fileTypesToCreateNetworkFrom = Arrays.asList(FilesSuffixesEnum.NODES, FilesSuffixesEnum.EDGES, FilesSuffixesEnum.TYPE_OF_EDGES, FilesSuffixesEnum.CONNECTIONS, FilesSuffixesEnum.TRAFFIC_LIGHT_LOGICS);
        //todo create fitness function

        for (int iterationNo = 0; iterationNo < maxIterations; iterationNo++) { //arba perdaryti kad kai atsakymų skirtumai yra mažesni nei epsilon
            String fileName = iterationNo == 0 ? baseFileName : baseFileName + iterationNo;
            creationRepo.createSumoConfigFile(fileName, routeFileName); // 3. setup SUMO configuration file

            creationRepo.runNetworkSimulationAndGetOutput(fileName, simulationOutputFileTypes); //get simulation output
            creationRepo.createPlainOutputFilesForEditingFromNetworkFile(fileName); //generates nodes, edges, connections, traffic light logic and type of edges files

            Network network = getNetworkFromGeneratedOutputNetworkFiles(fileName);
            networksWithComparableSimulationData.put(network, null);

            //todo analyze simulation output (and warnings from simulation progress)
            //todo edit network with genetic algorithm (fitness function, atrinkimas, kryzminimas, mutacija)

            fileName = baseFileName + (iterationNo + 1);
            xmlRepo.saveNetworkToXmlFiles(workingDirectory, fileName, network); //export edited network to xml files
            creationRepo.createNetworkFromNetworkFiles(fileName, fileTypesToCreateNetworkFrom);
        }
        //networksWithComparableSimulationData.isEmpty();
    }

    private static Network getNetworkFromGeneratedOutputNetworkFiles(String fileName) {
        XmlRepo xmlRepo = new XmlRepo();
        ParserRepo parserRepo = new ParserRepo();

        String fileNameBase = workingDirectory + fileName + SumoOutputDataFilesEnum.OUTPUT_FOR_EDITING.getFileEnd();

        Document nodeDocument = xmlRepo.readXml(fileNameBase + FilesSuffixesEnum.NODES.toString());
        Map<String, List<Map<String, Object>>> nodesAttributes = parserRepo.parseDocumentToObjects(nodeDocument);

        Document edgeDocument = xmlRepo.readXml(fileNameBase + FilesSuffixesEnum.EDGES.toString());
        Map<String, List<Map<String, Object>>> edgeAttributes = parserRepo.parseDocumentToObjects(edgeDocument);

        Document typeDocument = xmlRepo.readXml(fileNameBase + FilesSuffixesEnum.TYPE_OF_EDGES.toString());
        Map<String, List<Map<String, Object>>> typeAttributes = parserRepo.parseDocumentToObjects(typeDocument);

        Document connectionDocument = xmlRepo.readXml(fileNameBase + FilesSuffixesEnum.CONNECTIONS.toString());
        Map<String, List<Map<String, Object>>> connectionAttributes = parserRepo.parseDocumentToObjects(connectionDocument);

        Document tllDocument = xmlRepo.readXml(fileNameBase + FilesSuffixesEnum.TRAFFIC_LIGHT_LOGICS.toString());
        Map<String, List<Map<String, Object>>> tllAttributes = parserRepo.parseDocumentToObjects(tllDocument);

        List<Node> nodes = parserRepo.getNodesFromAttributeMap(nodesAttributes);
        List<Edge> edges = parserRepo.getEdgesFromEdgeAttributes(edgeAttributes);
        List<Roundabout> roundabouts = parserRepo.getRoundaboutsFromAttributeMap(edgeAttributes);
        List<Type> types = parserRepo.getTypesFromAttributeMap(typeAttributes);
        List<Connection> connections = parserRepo.getConnectionsFromAttributeMap(connectionAttributes);
        List<TlLogic> TlLogics = parserRepo.getTllogicsFromTllAttributeMap(tllAttributes);
        List<Connection> TLLogicsConnections = parserRepo.getConnectionsFromAttributeMap(tllAttributes);


        Network network = new Network();
        network.setNodes(nodes);
        network.setEdges(edges);
        network.setRoundabouts(roundabouts);
        network.setEdgeTypes(types);
        network.setConnections(connections);
        network.setTrafficLightLogics(TlLogics);
        network.setTrafficLightLogicsConnections(TLLogicsConnections);
        return network;
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
