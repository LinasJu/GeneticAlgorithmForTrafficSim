package lt.LinasJu;

public enum SumoCommandsEnum {
    NETGENERATE("netgenerate "), // for generating abstract road network
    NETCONVERT("netconvert "), // import and convert from Open Street Map or Visum
    SUMO("sumo "),
    PYTHON("py "),
    ROAD_NETWORK_INPUT(" --net-file %S "),
    SUMO_NET_FILE_INPUT(" -s %S"),

    NETWORK_OUTPUT_FILE_NAME(" -o %S.net.xml"),

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
