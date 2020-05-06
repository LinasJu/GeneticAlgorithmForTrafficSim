package lt.LinasJu.Entities.Nodes;

//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#right-of-way
public enum RightOfWayType {
    DEFAULT("default"),
    EDGE_PRIORITY("edgePriority");

    private final String rightOfWayType;

    RightOfWayType(final String rightOfWayType) {
        this.rightOfWayType = rightOfWayType;
    }

    @Override
    public String toString() {
        return rightOfWayType;
    }
    
}
