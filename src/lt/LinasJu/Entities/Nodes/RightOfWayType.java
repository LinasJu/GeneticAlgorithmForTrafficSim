package lt.LinasJu.Entities.Nodes;

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
