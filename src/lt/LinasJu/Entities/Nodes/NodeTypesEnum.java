package lt.LinasJu.Entities.Nodes;

//Node Connection types from https://sumo.dlr.de/userdoc/Networks/PlainXML.html#node_types
public enum NodeTypesEnum {
    PRIORITY("priority"),
    TRAFFIC_LIGHT("traffic_light"),
    RIGHT_BEFORE_LEFT("right_before_left"),
    UNREGULATED("unregulated"),
    TRAFFIC_LIGHT_UNREGULATED("traffic_light_unregulated"),
    PRIORITY_STOP("priority_stop"),
    ALLWAY_STOP("allway_stop"),
    RAIL_SIGNAL("rail_signal"),
    ZIPPER("zipper"),
    RAIL_CROSSING("rail_crossing"),
    TRAFFIC_LIGHT_RIGHT_ON_RED("traffic_light_right_on_red");

    private final String nodeType;

    NodeTypesEnum(final String nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String toString() {
        return nodeType;
    }
}