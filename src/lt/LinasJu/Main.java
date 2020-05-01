package lt.LinasJu;

import java.util.Date;

public class Main {

  public static String TEMP_EXPORT_LOCATION_OF_GENERATED_NETWORK = "C:\\SumoFiles\\GeneratedNetworks";
  public static float speedConvertionConstant = 3.6f;

  public static void main(String[] args) {
    RandomNetworkGenerationRepo generator = new RandomNetworkGenerationRepo();

    Date fileName = new Date();
    //1. generating random road network
    generator.generateRandomNetwork(TEMP_EXPORT_LOCATION_OF_GENERATED_NETWORK, fileName);
    /*for (int i = 0; i< 100; i++) {
      generator.generateRandomNetwork(TEMP_EXPORT_LOCATION_OF_GENERATED_NETWORK);
    }*/

  }
}
