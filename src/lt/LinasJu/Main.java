package lt.LinasJu;

import lt.LinasJu.Entities.Connections.Connection;
import lt.LinasJu.Entities.Edges.Edge;
import lt.LinasJu.Entities.Edges.Roundabout;
import lt.LinasJu.Entities.Network;
import lt.LinasJu.Entities.Nodes.Node;
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
    public static boolean isImportedNetwork;


    public static void main(String[] args) {
        if (args.length == 0) {
            throw new NullPointerException("program arguments must not be null! args[0] - working directory, args [1] - (optional), network file");
        }
        getWorkingDirectoryAndFileName(args);

        CreationRepo creationRepo = new CreationRepo(workingDirectory);
        XmlRepo xmlRepo = new XmlRepo();

        creationRepo.createBaseInputFiles(baseFileName, isImportedNetwork); // creates routes and SUMO config file (if no network is declared - then network too)

        for (int i = 0; i < 2; i++) {

        }

        List<SumoOutputDataFilesEnum> simulationOutputFileTypes = Arrays.asList(SumoOutputDataFilesEnum.FCD_TRACE_DATA, SumoOutputDataFilesEnum.RAW_VEHICLE_POSITION_DATA, SumoOutputDataFilesEnum.EMMISION_DATA);
        creationRepo.createSimulationOutputData(baseFileName, simulationOutputFileTypes); //get simulation output
        creationRepo.createPlainOutputFilesForEditing(baseFileName); //generates nodes, edges, connections, traffic light logic and type of edges files

        Network network = getNetworkFromGeneratedOutputNetworkFiles();

        //analyze simulation output (and warnings from simulation progress)
        //create fitness function
        //edit network with genetic algorithm
        xmlRepo.saveNetworkToXmlFiles(workingDirectory, baseFileName, network, String.valueOf(1)); //export edited network to xml files
    }

    private static Network getNetworkFromGeneratedOutputNetworkFiles() {
        XmlRepo xmlRepo = new XmlRepo();
        ParserRepo parserRepo = new ParserRepo();

        String fileNameBase = workingDirectory + baseFileName + SumoOutputDataFilesEnum.OUTPUT_FOR_EDITING.getFileEnd();

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
        List<Connection> connections = parserRepo.getConnectionsFromAttributeMap(connectionAttributes);//todo jeigu reikia padaryti map fromlane toLane
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
