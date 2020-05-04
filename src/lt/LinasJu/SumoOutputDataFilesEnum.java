package lt.LinasJu;

public enum SumoOutputDataFilesEnum {
  // Sumo Floating Car Data (FCD) Trace File (outputs location, speed, vehicle angle,
  // position, lane for every car at every time step)
  FCD_TRACE_DATA(" --fcd-output ", "sumoTrace"),
  //Raw vehicle positions dump (contains every edge, lane, vehicle positions, speeds, for each simulation step)
  RAW_VEHICLE_POSITION_DATA(" --netstate-dump ", "dump_file"),
  //Emission Output (CO2, CO, HC, NOX, fuel, electricity, noise, emitted by the vehicle in the actual simulation step)
  EMMISION_DATA(" --emission-output ", "emission_file"),
  //Full Output (dumps every information contained in the network, including emission, position, speed, lane, etc.)
  FULL_DATA(" --full-output ", "full_output"), // long computing time and DATA FILE size >>GB's!!!
  //SUMO Lane change Output (which vehicles, when and why changed lanes)
  LANE_CHANGE_DATA(" --lanechange-output ", "lane_change_file"),
  //SUMO VTK Output (generates Files in the well known VTK (Visualization Toolkit) format, to show the positions the speed value for every vehicle)
  VISUALIZATION_TOOLKIT_OUTPUT(" --vtk-output ", "vkt_file");

  private final String command;
  private final String fileEnd;

  SumoOutputDataFilesEnum(final String command, final String fileEnd) {
    this.command = command;
    this.fileEnd = fileEnd;
  }

  @Override
  public String toString() {
    return command;
  }

  public String getFileEndWithSuffix() {
    return fileEnd + FilesSuffixesEnum.XML;
  }
}
