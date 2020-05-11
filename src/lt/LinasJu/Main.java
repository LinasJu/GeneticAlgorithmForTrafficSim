package lt.LinasJu;

import lt.LinasJu.Entities.Nodes.Node;
import org.w3c.dom.Document;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new NullPointerException("program arguments must not be null! args[0] - working directory, args [1] - (optional), network file");
        }

        CmdRepo cmdRepo = new CmdRepo();
        CreationRepo creationRepo = new CreationRepo();
        XmlRepo xmlRepo = new XmlRepo();
        ParserRepo parserRepo = new ParserRepo();

        String workingDirectory = args[0];

        boolean isImportedNetwork = args.length == 2;
        String fileName;
        if (!isImportedNetwork) {
            List<String> nameAndDir = newFileName(workingDirectory);
            fileName = nameAndDir.get(0);
            workingDirectory = nameAndDir.get(1);
        } else {
            fileName = args[1];
        }

        List<Object> cmdAndProcess = cmdRepo.startCmdAtLocation(workingDirectory);
        PrintWriter cmd = (PrintWriter) cmdAndProcess.get(0);
        Process process = (Process) cmdAndProcess.get(1);

        //    collectConsoleOutputToFile(TEMP_WORKING_DIRECTORY, fileName);
        creationRepo.createInputFiles(cmd, workingDirectory, fileName, isImportedNetwork);

//        List<SumoOutputDataFilesEnum> simulationOutputDataEnums = Arrays.asList(SumoOutputDataFilesEnum.FCD_TRACE_DATA, SumoOutputDataFilesEnum.RAW_VEHICLE_POSITION_DATA, SumoOutputDataFilesEnum.EMMISION_DATA);
//        creationRepo.getSimulationOutputData(cmd, fileName, simulationOutputDataEnums);
//        creationRepo.generatePlainOutputOfNetwork(cmd, fileName);

        cmd.flush();
        cmd.close();
        process.waitFor();

        String nodeFileName = workingDirectory + fileName + SumoOutputDataFilesEnum.OUTPUT_FOR_EDITING.getFileEnd() + FilesSuffixesEnum.NODES.toString();
        Document nodeDocument = xmlRepo.readXml(nodeFileName);
        Map<String, List<Map<String, Object>>> nodesAttributes = parserRepo.parseDocumentToObjects(nodeDocument);
        List<Node> nodes = parserRepo.getNodesFromNodesAttributes(nodesAttributes);

        String edgeFileName = workingDirectory + fileName + SumoOutputDataFilesEnum.OUTPUT_FOR_EDITING.getFileEnd() + FilesSuffixesEnum.EDGES.toString();
        Document edgefile = xmlRepo.readXml(edgeFileName);
        Map<String, List<Map<String, Object>>> edgeAttributes = parserRepo.parseDocumentToObjects(edgefile);

        String typeFileName = workingDirectory + fileName + SumoOutputDataFilesEnum.OUTPUT_FOR_EDITING.getFileEnd() + FilesSuffixesEnum.TYPE_OF_EDGES.toString();
        Document typeDocument = xmlRepo.readXml(typeFileName);
        Map<String, List<Map<String, Object>>> typeAttributes = parserRepo.parseDocumentToObjects(typeDocument);

        String connectionFileName = workingDirectory + fileName + SumoOutputDataFilesEnum.OUTPUT_FOR_EDITING.getFileEnd() + FilesSuffixesEnum.CONNECTIONS.toString();
        Document connectionDocument = xmlRepo.readXml(connectionFileName);
        Map<String, List<Map<String, Object>>> connectionAttributes = parserRepo.parseDocumentToObjects(connectionDocument);

        String trafficLghtLogicFileName = workingDirectory + fileName + SumoOutputDataFilesEnum.OUTPUT_FOR_EDITING.getFileEnd() + FilesSuffixesEnum.TRAFFIC_LIGHT_LOGICS.toString();
        Document tllDocument = xmlRepo.readXml(trafficLghtLogicFileName);
        Map<String, List<Map<String, Object>>> tllAttributes = parserRepo.parseDocumentToObjects(tllDocument);

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
