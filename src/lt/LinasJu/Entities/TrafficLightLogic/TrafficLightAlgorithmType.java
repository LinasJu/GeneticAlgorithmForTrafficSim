package lt.LinasJu.Entities.TrafficLightLogic;

public enum TrafficLightAlgorithmType {
    STATIC("static"),
    ACTUATED("actuated"),
    DELAY_BASED("delay_based");

    private final String algorithmType;

    TrafficLightAlgorithmType(final String algorithmType) {
        this.algorithmType = algorithmType;
    }

    @Override
    public String toString() {
        return algorithmType;
    }
}
