package lt.LinasJu.Entities.TrafficLightLogic;

import java.util.HashMap;
import java.util.Map;

public enum TrafficLightAlgorithmType {
    STATIC("static"), ACTUATED("actuated"), DELAY_BASED("delay_based");

    private final String algorithmType;

    TrafficLightAlgorithmType(final String algorithmType) {
        this.algorithmType = algorithmType;
    }

    @Override
    public String toString() {
        return algorithmType;
    }

    //****** Reverse Lookup Implementation************//

    //Lookup table
    private static final Map<String, TrafficLightAlgorithmType> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static {
        for (TrafficLightAlgorithmType env : TrafficLightAlgorithmType.values()) {
            lookup.put(env.toString(), env);
        }
    }

    //This method can be used for reverse lookup purpose
    public static TrafficLightAlgorithmType get(String label) {
        return lookup.get(label);
    }

}
