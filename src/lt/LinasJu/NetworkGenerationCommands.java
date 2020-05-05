package lt.LinasJu;

/**
 * commands from https://sumo.dlr.de/userdoc/Networks/Abstract_Network_Generation.html
 */
public enum NetworkGenerationCommands {

    // first command in generating grid network
    GRID(" --grid"),
    // junctions quantity in x and y directions, or same in both axes
    GRID_NUMBER_X(GRID.toString().concat(".x-number=")),
    GRID_NUMBER_Y(GRID.toString().concat(".y-number=")),
    GRID_NUMBER_SAME_FOR_BOTH_AXES(GRID.toString().concat(".number=")),
    // distances between junctions in x and y directions, or same in both axes (in meters)
    GRID_LENGTH_X(GRID.toString().concat(".x-length=")),
    GRID_LENGTH_Y(GRID.toString().concat(".y-length=")),
    GRID_LENGTH_SAME_FOR_BOTH_AXES(GRID.toString().concat(".length=")),

    // building Spider network the centre of network will always be unregulated junction
    // first command in generating Spider network
    SPIDER(" --spider"),
    SPIDER_AXES_NUMBER(SPIDER.toString().concat(".arm-number=")), // default - 13
    SPIDER_NUMBER_OF_CIRCLES(SPIDER.toString().concat(".circle-number=")), // default - 20
    SPIDER_DISTANCE_BETWEEN_CIRCLES(SPIDER.toString().concat(".space-radius=")), // default - 100
    SPIDER_NO_CENTER_JUNCTION(SPIDER.toString().concat(".omit-center")), // optional

    // first command in generating Random network
    RAND(" --rand"),

    RAND_ITERATIONS(RAND.toString().concat(".iterations=")), // integer needed
    // optionals
    RAND_PROBABILITY_TO_BUILD_A_REVERSE_EDGE(RAND.toString().concat("bidi-probability=")), // float needed
    RAND_MAX_EDGE_LENGTH(RAND.toString().concat(".max-distance=")), // float needed
    RAND_MIN_EDGE_LENGTH(RAND.toString().concat(".min-distance=")), // float needed
    RAND_MIN_ANGLE_BETWEEN_EDGES(RAND.toString().concat(".min-angle")), // float needed
    RAND_NUMBER_OF_TRIES(RAND.toString().concat(".num-tries=")), // integer needed
    RAND_CONECTIVITY(RAND.toString().concat(".connectivity=")), // float needed
    RAND_NEIGHBOR_DISTANCE_1(RAND.toString().concat(".neighbor-dist1=")), // float needed
    RAND_NEIGHBOR_DISTANCE_2(RAND.toString().concat(".neighbor-dist2=")), // float needed
    RAND_NEIGHBOR_DISTANCE_3(RAND.toString().concat(".neighbor-dist3=")), // float needed
    RAND_NEIGHBOR_DISTANCE_4(RAND.toString().concat(".neighbor-dist4=")), // float needed
    RAND_NEIGHBOR_DISTANCE_5(RAND.toString().concat(".neighbor-dist5=")), // float needed
    RAND_NEIGHBOR_DISTANCE_6(RAND.toString().concat(".neighbor-dist6=")), // float needed

    DEFAULT_TYPE_OF_JUNCTION(" --default-junction-type-option"), // optional parameter for all networks

    //output options
    OUTPUT_PREFIX_TIME(" --output-prefix TIME"),
    PRECISION_NUMBER_AFTER_COMMA(" --precision"), //integer needed, default 2
    HUMAN_READABLE_TIME(" --human-readable-time true"),
    EASIER_NODE_READABILITY(" --alphanumerical-ids true"),

    //building defaults
    LANE_NUMBER(" --default.lanenumber "), //integer needed
    LANE_WIDTH(" --default.lanewidth "), //float needed
    SPEED(" -S "), //float needed
    EDGE_TYPE(" --default.type "); // string needed

    private final String command;

    NetworkGenerationCommands(final String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return command;
    }
}
