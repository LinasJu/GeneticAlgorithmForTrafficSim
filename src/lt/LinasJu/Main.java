package lt.LinasJu;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {

    public static String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new NullPointerException(
                    "program arguments must not be null! args[0] - working directory, args [1] - (optional), network file");
        }

        CmdRepo cmdRepo = new CmdRepo();
        CreationRepo creationRepo = new CreationRepo();

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

        PrintWriter cmd = cmdRepo.startCmdAtLocation(workingDirectory);

        //    collectConsoleOutputToFile(TEMP_WORKING_DIRECTORY, fileName);
        creationRepo.createInputFiles(cmd, workingDirectory, fileName, isImportedNetwork);

        List<SumoOutputDataFilesEnum> simulationOutputDataEnums =
                Arrays.asList(
                        SumoOutputDataFilesEnum.FCD_TRACE_DATA,
                        SumoOutputDataFilesEnum.RAW_VEHICLE_POSITION_DATA,
                        SumoOutputDataFilesEnum.EMMISION_DATA);

        creationRepo.getSimulationOutputData(cmd, fileName, simulationOutputDataEnums);

        creationRepo.generatePlainOutputOfNetwork(cmd, fileName);

        cmd.close();
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
        String fileName =
                new SimpleDateFormat(TIME_FORMAT)
                        .format(date); // general file name that will be used for same simulation files
        workingDir = workingDir.concat(fileName);

        boolean success =
                (new File(workingDir)).mkdirs(); // creates new folder to work in, with new simulation files
        if (!success) {
            System.out.println("Directory creation failed");
        }

        workingDir = workingDir.concat("\\");
        return new ArrayList<>(Arrays.asList(fileName, workingDir));
    }
}
