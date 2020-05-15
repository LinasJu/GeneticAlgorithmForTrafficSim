package lt.LinasJu;

import lt.LinasJu.Entities.Connections.Connection;
import lt.LinasJu.Entities.Edges.Edge;
import lt.LinasJu.Entities.Edges.Roundabout;
import lt.LinasJu.Entities.Network;
import lt.LinasJu.Entities.Nodes.Node;
import lt.LinasJu.Entities.TrafficLightLogic.TLLogic;
import lt.LinasJu.Entities.TypeOfEdge.Type;
import org.w3c.dom.Document;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    public static String fileName;
    public static String workingDirectory;
    public static boolean isImportedNetwork;


    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new NullPointerException("program arguments must not be null! args[0] - working directory, args [1] - (optional), network file");
        }

        CmdRepo cmdRepo = new CmdRepo();
        CreationRepo creationRepo = new CreationRepo();
        XmlRepo xmlRepo = new XmlRepo();
        ParserRepo parserRepo = new ParserRepo();

        getWorkingDirectoryAndFileName(args);

        List<Object> cmdAndProcess = cmdRepo.startCmdAtLocation(workingDirectory);
        PrintWriter cmd = (PrintWriter) cmdAndProcess.get(0);
        Process process = (Process) cmdAndProcess.get(1);

        //    collectConsoleOutputToFile(TEMP_WORKING_DIRECTORY, fileName);
        creationRepo.createInputFiles(cmd, workingDirectory, fileName, isImportedNetwork);

        List<SumoOutputDataFilesEnum> simulationOutputDataEnums = Arrays.asList(SumoOutputDataFilesEnum.FCD_TRACE_DATA, SumoOutputDataFilesEnum.RAW_VEHICLE_POSITION_DATA, SumoOutputDataFilesEnum.EMMISION_DATA);
        creationRepo.getSimulationOutputData(cmd, fileName, simulationOutputDataEnums);
        creationRepo.generatePlainOutputOfNetwork(cmd, fileName);

        cmd.flush();
        cmd.close();
        process.waitFor();

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
        List<Connection> connections = parserRepo.getConnectionsFromAttributeMap(connectionAttributes);//todo jeigu reikia padaryti map fromlane toLane
        List<TLLogic> TLLogics = parserRepo.getTllogicsFromTllAttributeMap(tllAttributes);
        List<Connection> TLLogicsConnections = parserRepo.getConnectionsFromAttributeMap(tllAttributes);

        Network network = setNetwork(nodes, edges, roundabouts, types, connections, TLLogics, TLLogicsConnections);


    }

    private static Network setNetwork(List<Node> nodes, List<Edge> edges, List<Roundabout> roundabouts, List<Type> types, List<Connection> connections, List<TLLogic> tlLogics, List<Connection> tlLogicsConnections) {
        Network network = new Network();
        network.setNodes(nodes);
        network.setEdges(edges);
        network.setRoundabouts(roundabouts);
        network.setEdgeTypes(types);
        network.setConnections(connections);
        network.setTrafficLightLogics(tlLogics);
        network.setTrafficLightLogicsConnections(tlLogicsConnections);
        return network;
    }

    private static void getWorkingDirectoryAndFileName(String[] args) {
        workingDirectory = args[0];

        isImportedNetwork = args.length == 2;
        if (!isImportedNetwork) {
            List<String> nameAndDir = newFileName(workingDirectory);
            fileName = nameAndDir.get(0);
            workingDirectory = nameAndDir.get(1);
        } else {
            fileName = args[1];
        }
    }

    public static void collectConsoleOutputToFile(String workingDir, String fileName) {
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(workingDir + fileName + "debugging.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(printStream);
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
