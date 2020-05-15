package lt.LinasJu.Entities.Nodes;

import java.util.HashMap;
import java.util.Map;

//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#right-of-way
public enum RightOfWayType {
    DEFAULT("default"), EDGE_PRIORITY("edgePriority");

    private final String rightOfWayType;

    RightOfWayType(final String rightOfWayType) {
        this.rightOfWayType = rightOfWayType;
    }

    @Override
    public String toString() {
        return rightOfWayType;
    }

    //****** Reverse Lookup Implementation************//

    //Lookup table
    private static final Map<String, RightOfWayType> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static {
        for (RightOfWayType env : RightOfWayType.values()) {
            lookup.put(env.toString(), env);
        }
    }

    //This method can be used for reverse lookup purpose
    public static RightOfWayType get(String label) {
        return lookup.get(label);
    }
}