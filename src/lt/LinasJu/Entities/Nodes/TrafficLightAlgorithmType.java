package lt.LinasJu.Entities.Nodes;

public enum TrafficLightAlgorithmType {
    STATIC("static"),
    ACTUATED("actuated");

    private final String algorithmType;

    TrafficLightAlgorithmType(final String algorithmType) {
        this.algorithmType = algorithmType;
    }

    @Override
    public String toString() {
        return algorithmType;
    }
}
