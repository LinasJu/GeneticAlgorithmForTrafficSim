package lt.LinasJu.Entities.Edges;

import java.util.HashMap;
import java.util.Map;

//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#spreadtype
public enum SpreadTypeEnum {
    RIGHT("right"), CENTER("center"), ROAD_CENTER("roadCenter");

    private final String spreadType;

    SpreadTypeEnum(String spreadType) {
        this.spreadType = spreadType;
    }

    @Override
    public String toString() {
        return spreadType;
    }

    //****** Reverse Lookup Implementation************//

    //Lookup table
    private static final Map<String, SpreadTypeEnum> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static {
        for (SpreadTypeEnum env : SpreadTypeEnum.values()) {
            lookup.put(env.toString(), env);
        }
    }

    //This method can be used for reverse lookup purpose
    public static SpreadTypeEnum get(String label) {
        return lookup.get(label);
    }
}