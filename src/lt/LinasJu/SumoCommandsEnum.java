package lt.LinasJu;

public enum SumoCommandsEnum {
  NETGENERATE("netgenerate"), // for generating abstract road network
  NETCONVERT("netconvert"), // import and convert from Open Street Map or Visum
  SUMO("sumo"),
  ROAD_NETWORK_INPUT(" --net-file %S"), // tinklas sukurtas su NETCONVERT arba NETGENERATE

  NETWORK_OUTPUT_FILE_NAME(" -o %S.net.xml");

  private final String command;

  SumoCommandsEnum(final String command) {
    this.command = command;
  }

  @Override
  public String toString() {
    return command;
  }
}
