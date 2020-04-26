package lt.LinasJu;

public enum SumoCommands {
    SUMO_HOME_PATH ("C:\\Program Files (x86)\\Eclipse\\Sumo"),
    NETGENERATE ("netgenerate"),
    NETCONVERT ("netconvert"),
    COMMAND_ROAD_NETWORK_INPUT ("--net-file %S"), // tinklas sukurtas su NETCONVERT arba NETGENERATE

    NETWORK_OUTPUT_FILE_NAME("-o %S.net.xml");

    private final String command;

    SumoCommands(final String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return command;
    }
}
