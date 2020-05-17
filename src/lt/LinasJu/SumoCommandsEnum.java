package lt.LinasJu;

public enum SumoCommandsEnum {

    NETGENERATE("netgenerate "), // for generating abstract road network
    NETCONVERT("netconvert "), // import and convert from Open Street Map or Visum
    SUMO("sumo "),
    PYTHON("py "),
    ROAD_NETWORK_INPUT("-n"),
    SUMO_NET_FILE_INPUT_FULL("-s"),

    NETWORK_OUTPUT_FILE_NAME("-o"),

    RANDOM_TRIPS_GENERATION_APP("\\randomTrips.py ");

    private final String command;

    SumoCommandsEnum(final String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return command;
    }
}
