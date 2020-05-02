package lt.LinasJu;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {

  public static String TEMP_WORKING_DIRECTORY = "C:\\SumoFiles\\GeneratedNetworks";
  public static String RANDOM_TRIPS_LOCATION = "\"C:\\Program Files (x86)\\Eclipse\\Sumo\\tools\"";
  public static String TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss";

  public static float speedConvertionConstant = 3.6f;

  public static void main(String[] args) {
    CreationRepo creationRepo = new CreationRepo();

    Date date = new Date();
    //general file name that will be used for same simulation files
    String fileName = new SimpleDateFormat(TIME_FORMAT).format(date);
    //1. generate random road network file
    creationRepo.generateRandomNetwork(TEMP_WORKING_DIRECTORY, fileName);

    //2. create random routes from network file
    creationRepo.generateRandomRoutes(TEMP_WORKING_DIRECTORY, RANDOM_TRIPS_LOCATION, fileName, 10f);

    //3. setup SUMO configuration file
    creationRepo.generateSumoConfigFile(fileName);

    // 4. generate output
    List<SumoOutputDataFilesEnum> outputDataFilesEnums =
        Arrays.asList(
            SumoOutputDataFilesEnum.FCD_TRACE_DATA,
            SumoOutputDataFilesEnum.RAW_VEHICLE_POSITION_DATA,
            SumoOutputDataFilesEnum.EMMISION_DATA);

    creationRepo.generateOutputFiles(TEMP_WORKING_DIRECTORY, fileName, outputDataFilesEnums);
  }
}
