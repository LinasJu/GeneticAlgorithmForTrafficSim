package lt.LinasJu;

public enum SumoOutputDataFilesEnum {
    // Sumo Floating Car Data (FCD) Trace File (outputs location, speed, vehicle angle,
    // position, lane for every car at every time step)
    FCD_TRACE_DATA("--fcd-output", "SumoTrace"),
    //Raw vehicle positions dump (contains every edge, lane, vehicle positions, speeds, for each simulation step)
    RAW_VEHICLE_POSITION_DATA("--netstate-dump", "Dump_file"),
    //Emission Output (CO2, CO, HC, NOX, fuel, electricity, noise, emitted by the vehicle in the actual simulation step)
    EMMISION_DATA("--emission-output", "Emission_file"),
    //Full Output (dumps every information contained in the network, including emission, position, speed, lane, etc.)
    FULL_DATA("--full-output", "Full_output"), // long computing time and DATA FILE size >>GB's!!!
    //SUMO Lane change Output (which vehicles, when and why changed lanes)
    LANE_CHANGE_DATA("--lanechange-output", "Lane_change_file"),
    //SUMO VTK Output (generates Files in the well known VTK (Visualization Toolkit) format, to show the positions the speed value for every vehicle)
    VISUALIZATION_TOOLKIT_OUTPUT("--vtk-output", "Vkt_file"),

    OUTPUT_FOR_EDITING("--plain-output-prefix=", "ForEditing");
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

    public String getFileEndWithSuffixXml() {
        return fileEnd + FilesSuffixesEnum.XML;
    }

    public String getFileEnd() {
        return fileEnd;
    }
}
