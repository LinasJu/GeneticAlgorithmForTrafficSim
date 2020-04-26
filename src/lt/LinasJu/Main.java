package lt.LinasJu;

public class Main {

  public static String TEMP_EXPORT_LOCATION_OF_GENERATED_NETWORK =
      "C:\\SumoFiles\\GeneratedNetworks";

  public static void main(String[] args) {
    RandomNetworkGenerationRepo generator = new RandomNetworkGenerationRepo();

    for (int i = 0; i< 100; i++) {
      generator.generateRandomNetwork(TEMP_EXPORT_LOCATION_OF_GENERATED_NETWORK);
    }
  }
}
